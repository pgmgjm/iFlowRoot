package pt.iflow.blocks;

import static pt.iflow.blocks.P17040.utils.FileGeneratorUtils.fillAtributtes;
import static pt.iflow.blocks.P17040.utils.FileGeneratorUtils.retrieveSimpleField;
import static pt.iflow.blocks.P17040.utils.FileValidationUtils.isValidDomainValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.P17040.utils.FileValidationUtils;
import pt.iflow.blocks.P17040.utils.ValidationError;

public class BlockP17040ValidateCENT extends BlockP17040Validate {

	public BlockP17040ValidateCENT(int anFlowId, int id, int subflowblockid, String filename) {
		super(anFlowId, id, subflowblockid, filename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<ValidationError> validate(UserInfoInterface userInfo, ProcessData procData, DataSource datasource,
			Integer crcId) throws SQLException {
		
		ArrayList<ValidationError> result = new ArrayList<>();
		
		List<Integer> infEntIdList = retrieveSimpleField(datasource, userInfo,
				"select infEnt.id from infEnt, comEnt, conteudo where infEnt.comEnt_id=comEnt.id and comEnt.conteudo_id = conteudo.id and conteudo.crc_id = {0} ",
				new Object[] { crcId });
		for (Integer infEntId : infEntIdList) {
			HashMap<String, Object> infEntValues = fillAtributtes(null, datasource, userInfo,
					"select * from infEnt where id = {0} ", new Object[] { infEntId });

			// idEnt
			if (retrieveSimpleField(datasource, userInfo,
					"select * from infEnt where comEnt_id = {0} and idEnt_id = {1}",
					new Object[] { infEntValues.get("comEnt_id"), infEntValues.get("idEnt_id") }).size() > 1)
				result.add(new ValidationError("EF010", "infEnt", "idEnt", infEntId));

			HashMap<String, Object> idEntValues = fillAtributtes(null, datasource, userInfo,
					"select * from idEnt where id = {0} ", new Object[] { infEntValues.get("idEnt_id") });
			if (StringUtils.equalsIgnoreCase("" + idEntValues.get("type"), "i1")
					&& StringUtils.isAlpha(idEntValues.get("nif_nipc").toString()))
				result.add(new ValidationError("EN008", "infEnt", "idEnt", infEntId));
			if (StringUtils.equalsIgnoreCase("" + idEntValues.get("type"), "i1")
					&& !FileValidationUtils.isValidNif(idEntValues.get("nif_nipc").toString()))
				result.add(new ValidationError("EN010", "infEnt", "idEnt", infEntId));

			// tpEnt
			String tpEntCheck = (String) infEntValues.get("tpEnt");
			if (tpEntCheck != null && !isValidDomainValue(userInfo, datasource, "T_TEN",tpEntCheck))
				result.add(new ValidationError("EN013", "infEnt", "tpEnt", infEntId));

			// nome
			String nomeCheck = (String) infEntValues.get("nome");
			if (StringUtils.isBlank(nomeCheck))
				result.add(new ValidationError("EN018", "infEnt", "nome", infEntId));

			// paisResd
			String paisResdCheck = (String) infEntValues.get("paisResd");
			if (StringUtils.isBlank(paisResdCheck))
				result.add(new ValidationError("EN033", "infEnt", "paisResd", infEntId));
			if (paisResdCheck != null && !isValidDomainValue(userInfo, datasource, "T_TER","" + paisResdCheck))
				result.add(new ValidationError("EN034", "infEnt", "paisResd", infEntId));

			// altIdEnt
			HashMap<String, Object> altIdEntValues = fillAtributtes(null, datasource, userInfo,
					"select * from altIdEnt where id = {0} ", new Object[] { infEntValues.get("altIdEnt_id") });
			if (StringUtils.equalsIgnoreCase("" + altIdEntValues.get("type"), "i1")
					&& StringUtils.isAlpha(altIdEntValues.get("nif_nipc").toString()))
				result.add(new ValidationError("EN008", "infEnt", "altIdEnt", infEntId));
			if (StringUtils.equalsIgnoreCase("" + altIdEntValues.get("type"), "i1")
					&& !FileValidationUtils.isValidNif(altIdEntValues.get("nif_nipc").toString()))
				result.add(new ValidationError("EN010", "infEnt", "altIdEnt", infEntId));

			// dadosEnt type=t1
			if (retrieveSimpleField(datasource, userInfo, "select id from dadosEntt1 where infEnt_id = {0} ",
					new Object[] { infEntId }).size() == 1) {
				HashMap<String, Object> dadosEntt1Values = fillAtributtes(null, datasource, userInfo,
						"select * from dadosEntt1 where infEnt_id = {0} ", new Object[] { infEntId });
				// dtNasc
				Date dtNascCheck = (Date) dadosEntt1Values.get("dtNasc");
				if (dtNascCheck == null)
					result.add(
							new ValidationError("EN030", "dadosEnt", "dtNasc", (Integer) dadosEntt1Values.get("id")));
				if (dtNascCheck != null && dtNascCheck.before(new Date()))
					result.add(
							new ValidationError("EN035", "dadosEnt", "dtNasc", (Integer) dadosEntt1Values.get("id")));
				// genero
				String generoCheck = (String) dadosEntt1Values.get("genero");
				if (generoCheck != null && !isValidDomainValue(userInfo, datasource, "T_GEN","" + generoCheck))
					result.add(
							new ValidationError("EN036", "dadosEnt", "genero", (Integer) dadosEntt1Values.get("id")));
				// sitProf
				String sitProfCheck = (String) dadosEntt1Values.get("sitProf");
				if (sitProfCheck != null && !isValidDomainValue(userInfo, datasource, "T_SPF","" + sitProfCheck))
					result.add(
							new ValidationError("EN037", "dadosEnt", "sitProf", (Integer) dadosEntt1Values.get("id")));
				// agregFam
				Integer agregFamCheck = (Integer) dadosEntt1Values.get("agregFam");
				if (agregFamCheck != null && agregFamCheck <= 0)
					result.add(
							new ValidationError("EN038", "dadosEnt", "sitProf", (Integer) dadosEntt1Values.get("id")));
				// habLit
				String habitLitcheck = (String) dadosEntt1Values.get("habLit");
				if (habitLitcheck != null && !isValidDomainValue(userInfo, datasource, "T_HAL","" + habitLitcheck))
					result.add(
							new ValidationError("EN039", "dadosEnt", "habLit", (Integer) dadosEntt1Values.get("id")));
				// nacionalidade
				String nacionalidadeCheck = (String) dadosEntt1Values.get("nacionalidade");
				if (StringUtils.isBlank(nacionalidadeCheck))
					result.add(new ValidationError("EN031", "dadosEnt", "nacionalidade",
							(Integer) dadosEntt1Values.get("id")));
				if (!StringUtils.isBlank(nacionalidadeCheck)
						&& !isValidDomainValue(userInfo, datasource, "T_TER","" + nacionalidadeCheck))
					result.add(new ValidationError("EN032", "dadosEnt", "nacionalidade",
							(Integer) dadosEntt1Values.get("id")));

			}
			// dadosEnt type=t2
			else {
				HashMap<String, Object> dadosEntt2Values = fillAtributtes(null, datasource, userInfo,
						"select * from dadosEntt2 where infEnt_id = {0} ", new Object[] { infEntId });
				// formJurid
				String formJuridcheck = (String) dadosEntt2Values.get("formJurid");
				if (formJuridcheck != null && !isValidDomainValue(userInfo, datasource, "T_JUR","" + formJuridcheck))
					result.add(new ValidationError("EN044", "dadosEnt", "formJurid",
							(Integer) dadosEntt2Values.get("id")));
				// PSE
				String PSECheck = (String) dadosEntt2Values.get("PSE");
				if (PSECheck != null && !isValidDomainValue(userInfo, datasource, "T_PSE","" + PSECheck))
					result.add(new ValidationError("EN045", "dadosEnt", "PSE", (Integer) dadosEntt2Values.get("id")));
				// SI
				String SICheck = (String) dadosEntt2Values.get("SI");
				if (SICheck != null && !isValidDomainValue(userInfo, datasource, "T_STI","" + SICheck))
					result.add(new ValidationError("EN049", "dadosEnt", "SI", (Integer) dadosEntt2Values.get("id")));
			}

			// lstDocId
			List<Integer> docIdList = retrieveSimpleField(datasource, userInfo,
					"select docId.id from docId where infEnt_id = {0} ", new Object[] { infEntId });
			for (Integer docIdId : docIdList) {
				HashMap<String, Object> docIdValues = fillAtributtes(null, datasource, userInfo,
						"select * from docId where id = {0} ", new Object[] { docIdId });
				// tpDoc
				String tpDocCheck = (String) docIdValues.get("tpDoc");
				if (tpDocCheck != null && !isValidDomainValue(userInfo, datasource, "T_TID","" + tpDocCheck))
					result.add(new ValidationError("EN023", "docId", "tpDoc", (Integer) docIdValues.get("id")));
				// numDoc
				String numDocCheck = (String) docIdValues.get("numDoc");
				if (StringUtils.isBlank(numDocCheck))
					result.add(new ValidationError("EN025", "docId", "numDoc", (Integer) docIdValues.get("id")));
				// paisEmissao
				String paisEmissaoCheck = (String) docIdValues.get("paisEmissao");
				if (paisEmissaoCheck != null
						&& !isValidDomainValue(userInfo, datasource, "T_TER","" + paisEmissaoCheck))
					result.add(new ValidationError("EN026", "docId", "paisEmissao", (Integer) docIdValues.get("id")));
				// dtEmissao
				Date dtEmissaoCheck = (Date) docIdValues.get("dtEmissao");
				if (dtEmissaoCheck != null && dtEmissaoCheck.after(new Date()))
					result.add(new ValidationError("EN050", "docId", "dtEmissao", (Integer) docIdValues.get("id")));
				// dtValidade
				Date dtValidadeCheck = (Date) docIdValues.get("dtValidade");
				if (dtEmissaoCheck != null && dtEmissaoCheck != null && dtValidadeCheck.before(dtEmissaoCheck))
					result.add(new ValidationError("EN051", "docId", "dtValidade", (Integer) docIdValues.get("id")));
			}
		}
		return result;
	}

}