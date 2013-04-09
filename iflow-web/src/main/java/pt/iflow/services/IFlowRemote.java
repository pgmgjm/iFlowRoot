/*
 * <p>Title: UniFlowRemote.java</p> <p>Description: </p> <p>Copyright:
 * Copyright (c) Sep 2, 2005</p> <p>Company: iKnow</p> @author Pedro Monteiro
 * 
 * @version 1.0
 */

package pt.iflow.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormService;
import pt.iflow.api.blocks.MessageBlock;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.presentation.FlowMenu;
import pt.iflow.api.presentation.FlowMenuItems;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.services.types.ActivitySet;
import pt.iflow.api.services.types.DataElement;
import pt.iflow.api.services.types.DataElementSet;
import pt.iflow.api.services.types.FlowDataConvert;
import pt.iflow.api.services.types.FlowDataConvertSet;
import pt.iflow.api.services.types.StringSet;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.blocks.BlockFormulario;
import pt.iflow.flows.FlowData;
import pt.iknow.utils.StringUtilities;

public class IFlowRemote {

  public static final String SUCCESS = "OK";
  public static final String FAILURE = "FAIL";

  /**
   * Method to be called (as an Axis WebService) to list owner flows
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @return List of the flows owned by the user.
   * @throws Exception
   */
  public FlowDataConvertSet listFlows(String user, String password) throws Exception {
    FlowDataConvertSet fdcs = new FlowDataConvertSet();
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "listFlows", "Wrong user/password");
      throw new Exception("Wrong user/password");
    } else {
      Logger.warning(user, this, "listFlows", "UserInfo obtained");
    }
    // Method listFlows of FlowHolder Class don't return all datas
    Map<String, IFlowData> hmFlows = new HashMap<String, IFlowData>();
    FlowApplications fa = BeanFactory.getFlowApplicationsBean();
    Logger.warning(user, this, "listFlows", "All flows obtained");

    FlowMenu fm = fa.getAllApplicationOnlineFlows(ui);// (userInfo, null);
    Logger.warning(user, this, "listFlows", "All user menus and flows online obtained");

    Collection<FlowAppMenu> fams = fm.getAppMenuList();
    Logger.warning(user, this, "listFlows", "User menu list obtained");

    Iterator<FlowAppMenu> it = fams.iterator();
    Logger.warning(user, this, "listFlows", "Iterator to user menu list obtained");
    while (it != null && it.hasNext()) {
      FlowAppMenu fam = it.next();
      FlowMenuItems fmis = fam.getMenuItems();
      List<IFlowData> al = fmis.getFlows();
      for (int i = 0; al != null && i < al.size(); i++) {
        IFlowData ifd = al.get(i);
        String sFlowId = String.valueOf(ifd.getId());
        hmFlows.put(sFlowId, ifd);
      }
    }
    Logger.warning(user, this, "listFlows", "User IFlowData Map completed");

    Collection<IFlowData> cifd = hmFlows.values();
    Logger.warning(user, this, "listFlows", "User IFlowData Collection obtained");

    FlowData[] fds = (FlowData[]) cifd.toArray(new FlowData[0]);
    Logger.warning(user, this, "listFlows", "User FlowData Array obtained");

    FlowDataConvert[] fdc = FlowData.toFlowDataConvertArray(fds);
    Logger.warning(user, this, "listFlows", "User FlowDataConvert Array obtained");

    fdcs.setResult(fdc);
    Logger.warning(user, this, "listFlows", "User FlowDataConvertSet filled");
    return fdcs;
  }

  // public String processXmlForm(String user, String password, int flowid, int pid, int subpid, String xmlForm, String op)
  // throws Exception {
  // String popupReturnBlockId = null;
  //
  // String title = "Formul&aacute;rio";
  // // use of fdFormData defined in /inc/defs.jsp
  // String sOp = fdFormData.getParameter("op");
  // if (sOp == null) {
  // sOp = "0";
  // }
  // int op = Integer.parseInt(sOp);
  //
  // Block bBlockJSP = null;
  //
  // String currMid = String.valueOf(pm.getModificationId(userInfo, procData.getProcessHeader()));
  //
  // HashMap<String, String> hmHidden = new HashMap<String, String>();
  // hmHidden.put("subpid", String.valueOf(subpid));
  // hmHidden.put("pid", String.valueOf(pid));
  // hmHidden.put("flowid", String.valueOf(flowid));
  // hmHidden.put("op", String.valueOf(op));
  // hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
  // hmHidden.put("_serv_field_", "-1");
  // hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
  // request.setAttribute(Const.sMID_ATTRIBUTE, currMid);
  //
  // Flow flow = BeanFactory.getFlowBean();
  // try {
  //
  // bBlockJSP = flow.getBlock(userInfo, procData);
  //
  // // To appear blockId in title
  // title = bBlockJSP.getDescription(userInfo, procData);
  // if (Const.nMODE == Const.nDEVELOPMENT) {
  // title = title + " block" + bBlockJSP.getId();
  // }
  //
  // if (bBlockJSP.getClass().getName().indexOf("BlockFormulario") == -1) {
  // throw new Exception("Not BlockFormulario!");
  // }
  // } catch (Exception e) {
  // // send to main page...
  // // not able to get flow or process is not in jsp state (if
  // // a casting exception occurs..)
  // ServletUtils.sendEncodeRedirect(response, "../flow_error.jsp");
  // return;
  // }
  //
  // // OP: 0 - entering page/reload
  // // 1 - unused
  // // 2 - save
  // // 3 - next
  // // 4 - cancel
  // // 5 - service print
  // // 6 - service print field
  // // 7 - service export field
  // // 8 - only process form
  // // 9 - return to parent
  //
  // // check write permissions...
  // if (!flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.WRITE_PRIV)
  // && !flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.SUPERUSER_PRIV)) {
  // ServletUtils.sendEncodeRedirect(response, "../nopriv.jsp?flowid=" + flowid);
  // return;
  // }
  //
  // if (bBlockJSP != null && op == 0) {
  // // Variables
  // String description = title;
  // // TODO: corrigir isto
  // String url = "Form/form.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid;
  //
  // Logger.trace(this, "before", login + " call with subpid=" + subpid + ",pid=" + pid + ",flowid=" + flowid);
  //
  // String nextPage = url;
  //
  // Activity activity = null;
  // String stmp = null;
  //
  // try {
  // // Get the ProcessManager EJB
  //
  // activity = new Activity(login, flowid, pid, subpid, 0, 0, description, Block.getDefaultUrl(userInfo, procData), 1);
  // activity.setRead();
  // activity.mid = procData.getMid();
  // pm.updateActivity(userInfo, activity);
  //
  // } catch (Exception e) {
  // Logger.error(login, this, "before", procData.getSignature() + "Caught an unexpected exception scheduling activities: "
  // + e.getMessage(), e);
  // }
  // }
  //
  // if (bBlockJSP != null && op == 9) {
  // // return to parent
  // ProcessData pdReturn = (ProcessData) session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
  // session.setAttribute(Const.SESSION_PROCESS + flowExecType, pdReturn);
  // String next_page = flow.nextBlock(userInfo, pdReturn);
  //
  // if (next_page == null) {
  // next_page = sURL_PREFIX + "flow_error.jsp" + "?" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // } else {
  // next_page = sURL_PREFIX + next_page + "&" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // }
  //
  // ServletUtils.sendEncodeRedirect(response, next_page);
  // return;
  //
  // }
  //
  // Object[] oa = null;
  // if (bBlockJSP != null && (op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == FormOperations.OP_ONCHANGE_SUBMIT)) {
  //
  // String formMid = fdFormData.getParameter(Const.sMID_ATTRIBUTE);
  // request.setAttribute(Const.sMID_ATTRIBUTE, formMid);
  // boolean procAccessOk = StringUtils.isBlank(formMid) || StringUtils.equals(currMid, formMid);
  //
  // if (procAccessOk) {
  // oa = new Object[5];
  // oa[0] = userInfo;
  // oa[1] = procData;
  // oa[2] = fdFormData;
  // oa[3] = new ServletUtils(request, response);
  // oa[4] = (op == FormOperations.OP_GENERATE_FORM || op == FormOperations.OP_ONCHANGE_SUBMIT);
  //
  // // 3: processForm
  // procData = (ProcessData) bBlockJSP.execute(3, oa);
  //
  // oa = new Object[1];
  // oa[0] = procData;
  // // 4: hasError
  // if (!((Boolean) bBlockJSP.execute(4, oa)).booleanValue()) {
  //
  // if (op == 2 || (pid != Const.nSESSION_PID && (op == 5 || op == 6 || op == 7 /* || op == 8 */))) {
  // // save dataset if op = 2 (save) or refresh(8)/print(5,6)/export(7) and process is in DB
  // // don't save it if op = 3 because nextBlock does it!
  // int mid = flow.saveDataSet(userInfo, procData, request);
  // currMid = String.valueOf(mid);
  // // now don't forget to update flowid and pid and subpid
  // flowid = procData.getFlowId();
  // pid = procData.getPid();
  // subpid = procData.getSubPid();
  // hmHidden.put("subpid", String.valueOf(subpid));
  // hmHidden.put("pid", String.valueOf(pid));
  // hmHidden.put("flowid", String.valueOf(flowid));
  // hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
  // hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
  // request.setAttribute(Const.sMID_ATTRIBUTE, currMid);
  // }
  //
  // boolean autoAdvance = (op == FormOperations.OP_ONCHANGE_SUBMIT ? ((Boolean) bBlockJSP.execute(14, oa)).booleanValue()
  // : false);
  // if (op == 3 || autoAdvance) {
  // String next_page = flow.nextBlock(userInfo, procData);
  //
  // // Caso esteja a correr em popup e o bloco não seja de popup, sair no erro
  // if (popupReturnBlockId != null && !flow.getBlock(userInfo, procData).canRunInPopupBlock()) {
  // next_page = sURL_PREFIX + "flow_popup_error.jsp" + "?" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // } else {
  // boolean bStay = false;
  // if (StringUtils.isNotEmpty(procData.getAppData(Const.STAY_IN_PAGE))) {
  // bStay = true;
  // } else {
  // if (next_page == null) {
  // next_page = sURL_PREFIX + "flow_error.jsp" + "?" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // } else {
  // bStay = false;
  // next_page = sURL_PREFIX + next_page + "&" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // }
  // }
  // if (!bStay) {
  // if (!procData.isInDB()) {
  // session.setAttribute(Const.SESSION_PROCESS + flowExecType, procData);
  // }
  // ServletUtils.sendEncodeRedirect(response, next_page);
  // return;
  // }
  // }
  // }
  // }
  // } else {
  // oa = new Object[2];
  // oa[0] = procData;
  // oa[1] = messages.getString("form.proc_change_error");
  // // 5: setError
  // bBlockJSP.execute(5, oa);
  // }
  // } // op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == 8
  // else if (bBlockJSP != null && op == 4) {
  // // cancel proc
  // String next_page = flow.endProc(userInfo, procData);
  // if (next_page == null) {
  // next_page = sURL_PREFIX + "flow_error.jsp" + "?" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // } else {
  // next_page = sURL_PREFIX + next_page + "&" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
  // }
  // ServletUtils.sendEncodeRedirect(response, next_page);
  // return;
  // } // op == 4
  //
  // String sHtml = "";
  // String sFormName = "";
  //
  // if (bBlockJSP != null) {
  // // return to parent additional stuff
  // boolean bRemoveParentProc = false;
  // ProcessData pdReturn = (ProcessData) session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
  // if (pdReturn != null) {
  // String sSwitchFlag = pdReturn.getTempData(Const.sSWITCH_PROC_FLAG);
  // String sRetProcFid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_FID);
  // String sRetProcPid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_PID);
  // String sRetProcSubPid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_SUBPID);
  //
  // if (sSwitchFlag == null || !sSwitchFlag.equals("true") || sRetProcFid == null || sRetProcPid == null
  // || sRetProcSubPid == null) {
  // bRemoveParentProc = true;
  // } else {
  // if (!sRetProcFid.equals(String.valueOf(flowid)) || !sRetProcPid.equals(String.valueOf(pid))
  // || !sRetProcSubPid.equals(String.valueOf(subpid))) {
  // bRemoveParentProc = true;
  // } else {
  // // enable switch proc return to parent
  // procData.setTempData(Const.sSWITCH_PROC_RETURN_PARENT, "true");
  // }
  // }
  // } else {
  // bRemoveParentProc = true;
  // }
  //
  // if (bRemoveParentProc) {
  // procData.setTempData(Const.sSWITCH_PROC_RETURN_PARENT, null);
  // session.removeAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
  // }
  //
  // oa = new Object[4];
  // oa[0] = userInfo;
  // oa[1] = procData;
  // oa[2] = hmHidden;
  // oa[3] = new ServletUtils(response);
  // // 2: generateForm
  // sHtml = (String) bBlockJSP.execute(2, oa);
  //
  // // 7: var FORM_NAME
  // sFormName = (String) bBlockJSP.execute(7, null);
  // }
  //
  // return null;
  // }

  /**
   * Retrieves the design of a form in XML format
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id
   * @return The design data of a Form in XML format
   * @throws Exception
   */
  public String generateXmlForm(String user, String password, int flowid, int pid, int subpid) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    Flow f = BeanFactory.getFlowBean();
    FlowType flowType = BeanFactory.getFlowBean().getFlowType(ui, flowid);// Op
    HashMap<String, String> hmHidden = new HashMap<String, String>();
    String flowExecType = "";
    String currMid;
    ProcessManager pm = null;
    ProcessData pd = null;
    Block b = null;
    BlockFormulario bf = null;
    String sXml = "";
    if (!ui.isLogged()) {
      Logger.warning(user, this, "generateXmlForm", "Wrong user/password");
      throw new Exception("Wrong user/password");
    } else {
      Logger.warning(user, this, "generateXmlForm", "UserInfo obtained");
    }
    try {
      pm = BeanFactory.getProcessManagerBean();
      pd = pm.getProcessData(ui, new ProcessHeader(flowid, pid, subpid));
      Logger.warning(user, this, "generateXmlForm", "ProcessData obtained");
    } catch (Exception e) {
      Logger.warning(user, this, "generateXmlForm", "Caught an unexpected exception: " + e.getMessage());
    }
    try {
      // Get current block
      b = f.getBlock(ui, pd);
      // Only want block form
      if (b.getClass().getName().indexOf("BlockFormulario") != -1) {
        bf = (BlockFormulario) b;
        Logger.warning(user, this, "generateXmlForm", "BlockFormulario obtained");

        // Values are copied from BlockFormulario.exportFieldToSpreadSheet Line: 2695
        // String sXml = generateForm(this, userInfo, procData, null, true, FormService.EXPORT, nField, response);
        // Where nField = -1 because we want all formulary fields
        // FormService.EXPORT is a option to export to xml
        // ShowButtons true let show buttons as NEXT although anService was FormService.EXPORT
        // commonly flowType = WORKFLOW, if flowType isn't SEARCH or REPORT, flowExecType will be ""
        if (FlowType.SEARCH.equals(flowType)) {
          flowExecType = "SEARCH";
        } else if (FlowType.REPORTS.equals(flowType)) {
          flowExecType = "REPORT";
        }
        // Current modification id for a given process (mid from process table)
        currMid = String.valueOf(pm.getModificationId(ui, pd.getProcessHeader()));

        hmHidden.put("subpid", String.valueOf(subpid));
        hmHidden.put("pid", String.valueOf(pid));
        hmHidden.put("flowid", String.valueOf(flowid));
        hmHidden.put("op", "0"); // See flow.jsp lines 58 to 67
        hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
        hmHidden.put("_serv_field_", "-1");
        hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
        sXml = BlockFormulario.generateForm(bf, ui, pd, hmHidden, true, FormService.EXPORT, -1, null, true);
        Logger.warning(user, this, "generateXmlForm", "Form XML obtained");

      } else { // Isn't BlockForm, others valid types: ForwardTo, ForwardUp, Fim
        if (b.getClass().getName().indexOf("BlockForwardTo") != -1 || b.getClass().getName().indexOf("BlockForwardUp") != -1) {
          Logger.warning(user, this, "generateXmlForm", b.getClass().getName() + " is BlockForwardTo!");
          FlowSettings settings = BeanFactory.getFlowSettingsBean();
          FlowSetting setting = settings.getFlowSetting(flowid, Const.sSHOW_SCHED_USERS);
          String sShowUsers = (setting == null ? null : setting.getValue());
          IMessages messages = ui.getMessages();
          List<String> lMessages = new ArrayList<String>();
          String message = ((MessageBlock) b).getMessage(ui, pd);
          if (!StringUtils.isBlank(message)) {
            lMessages.add(message);
          }
          if (StringUtils.isNotBlank(sShowUsers)
              && StringUtilities.isAnyOfIgnoreCase(sShowUsers, new String[] { Const.sSHOW_YES, "sim", "yes", "true", "1" })) {
            // It must show users
            Iterator<Activity> it = pm.getProcessActivities(ui, flowid, pid, subpid);
            if (it != null) {
              lMessages.add(messages.getString("proc_info.msg.fwshownuser"));
              AuthProfile aptmp = BeanFactory.getAuthProfileBean();
              Activity a = null;
              while (it.hasNext()) {
                a = it.next();
                UserData ud = aptmp.getUserInfo(a.userid);
                if (ud != null) {
                  lMessages.add(ud.getName());
                }
              }
            }
          } else {
            // It must not show users
            if (StringUtils.isBlank(message)) {
              lMessages.add(messages.getString("proc_info.msg.fwhiddenuser"));
            }
          }
          sXml = generateXmlBlock(lMessages);
          Logger.warning(user, this, "generateXmlForm", "Block XML obtained");

        } else if (b.getClass().getName().indexOf("BlockFim") != -1) {
          sXml = b.getClass().getName();
        } else {
          Logger.warning(user, this, "generateXmlForm", b.getClass().getName() + " isn't valid Block!");
          sXml = b.getClass().getName();
          throw new Exception(b.getClass().getName() + " isn't valid Block!");
        }
      }
    } catch (Exception e) {
      Logger.warning(user, this, "generateXmlForm", "Not Block!");
      if (pd.isClosed()) {
        sXml = "Process finished";
      } else {
        throw new Exception("Without Block and process unfinished!");
      }
    }

    byte[] utf8 = new String(sXml.getBytes(), "ISO-8859-15").getBytes("UTF-8");
    sXml = new String(utf8);
    return sXml;
  }

  private String generateXmlBlock(List<String> messages) {
    StringBuffer sXml = new StringBuffer();
    Iterator<String> it = messages.iterator();
    sXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sXml.append("<form>");
    sXml.append("<blockdivision><columndivision>");
    while (it.hasNext()) {
      String message = it.next();
      sXml.append("<field>");
      sXml.append("<type>textmessage</type><text>");
      sXml.append(message);
      sXml.append("</text><cssclass></cssclass><align>left</align><even_field>false</even_field>");
      sXml.append("</field>");
    }
    sXml.append("</columndivision></blockdivision>");
    sXml.append("</form>");

    return sXml.toString();
  }

  /**
   * This method is used to call the the method startFlow from an iFlow block. Since iflow blocks only uses simple data types, this
   * method converts String Arrays to a DataElementSet and calls the starFlow method with that data.
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow to be started
   * @param names
   *          List of the names of the variables to be initialized
   * @param values
   *          List of the values to assign to the variables
   * @param types
   *          List of the variables types
   * @return a String contaning the id of the process created by the Flow
   * @throws Exception
   */
  public String startFlowFromBlock(String user, String password, int flowid, String[] names, String[] values, String[] types)
      throws Exception {
    DataElementSet desFields = null;

    if (names != null && names.length > 0) {
      desFields = new DataElementSet();
      DataElement[] result = new DataElement[names.length];
      for (int i = 0; i < names.length; i++) {
        result[i] = new DataElement(names[i], "", "String");
        if (values.length > i && values[i] != null)
          result[i].setValue(values[i]);
        if (types.length > i && types[i] != null)
          result[i].setType(types[i]);
      }
      desFields.setResult(result);
    }

    return startFlow(user, password, flowid, desFields);
  }

  /**
   * Method to be called (as an Axis WebService) to initiate a flow
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow to be started
   * @param desFields
   *          Set containing the variables names, values and types, to initialize the new process
   * @return a String contaning the id of the process created by the Flow
   * @throws Exception
   */
  public String startFlow(String user, String password, int flowid, DataElementSet desFields) throws Exception {

    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "startFlow", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }

    Flow flow = BeanFactory.getFlowBean();

    ProcessManager pm = BeanFactory.getProcessManagerBean();

    if (!ui.isOrgAdmin() && !flow.checkUserFlowRoles(ui, flowid, "" + FlowRolesTO.CREATE_PRIV)) {
      Logger.warning(user, this, "startFlow", "Insufficient roles to start flowid " + flowid);
      throw new Exception("Insufficient roles to start flowid " + flowid);
    }

    ProcessData procData = pm.createProcess(ui, flowid);
    if (procData == null) {
      Logger.warning(user, this, "startFlow", "Error creating process");
      throw new Exception("Error creating process");
    }

    if (desFields != null && desFields.getResult() != null) {
      DataElement[] deFields = desFields.getResult();
      for (int i = 0; deFields != null && i < deFields.length; i++) {
        String key = deFields[i].getName();
        String type = deFields[i].getType();
        if (type == null)
          continue;

        try {
          procData.parseAndSet(key, deFields[i].getValue());
        } catch (ParseException e) {
        }
      }
    }

    flow.nextBlock(ui, procData);
    return "" + procData.getPid();
  }

  /**
   * Gets the state (current activity) of the process
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id.
   * @return the process state, that is, the description of its current activity.
   * @throws Exception
   */
  public String getProcessState(String user, String password, int flowid, int pid, int subpid) throws Exception {
    String retObj = "";

    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "getProcessState", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      ProcessHeader ph = new ProcessHeader(flowid, pid, subpid);
      retObj = pm.getProcessState(ui, ph);

      if (retObj.trim().equals("")) {
        retObj = "Error: State not found for flowid[" + flowid + "] " + "pid[" + pid + "] " + "subpid[" + subpid + "]";
      }

    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(user, this, "getProcessState", "Error: caught exception: " + e.getMessage());
      throw new Exception("Error: caught exception: " + e.getMessage());
    }

    return retObj;
  }

  /**
   * Retrieves the worklist history of a process
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id
   * @return All the activities of a process
   * @throws Exception
   */
  public ActivitySet getProcessActivityHistory(String user, String password, int flowid, int pid, int subpid) throws Exception {
    ActivitySet retObj = null;

    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "getProcessActivityHistory", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      List<Activity> lit = pm.getProcessActivityHistory(ui, flowid, pid, subpid);
      if (lit != null && !lit.isEmpty()) {
        retObj = new ActivitySet();
        retObj.setResult(lit.toArray(new Activity[lit.size()]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(user, this, "getProcessActivity", "Error: caught exception: " + e.getMessage());
      throw new Exception("Error: caught exception: " + e.getMessage());
    }

    return retObj;
  }

  /**
   * Retrieves a user's current list of activites
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @return All the activities opened for the user
   * @throws Exception
   */
  public ActivitySet getUserActivities(String user, String password) throws Exception {
    ActivitySet retObj = null;

    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "getUserActivities", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      Iterator<Activity> lit = pm.getUserActivities(ui);

      List<Activity> altmp = new ArrayList<Activity>();

      while (lit != null && lit.hasNext()) {
        Activity a = lit.next();
        altmp.add(a);
      }

      if (altmp.size() > 0) {
        Activity[] atmp = new Activity[altmp.size()];
        retObj = new ActivitySet();
        retObj.setResult(altmp.toArray(atmp));
      }

    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(user, this, "getUserActivities", "Error: caught exception: " + e.getMessage());
      throw new Exception("Error: caught exception: " + e.getMessage());
    }

    return retObj;
  }

  /**
   * Retrieves process data for given process
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id
   * @param ssFields
   *          List of the names of the variables to get. Must be empty to get all variables
   * @return Set with the names, values and types of all the variables of the process
   * @throws Exception
   */
  public DataElementSet getProcessData(String user, String password, int flowid, int pid, int subpid, StringSet ssFields)
      throws Exception {
    DataElementSet retObj = null;

    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "getProcessData", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }
    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      ProcessHeader[] pdaHeaders = { new ProcessHeader(flowid, pid, subpid) };
      String[] saFields = null;
      if (ssFields != null && ssFields.getResult() != null) {
        saFields = ssFields.getResult();
        Logger.debug("", this, "", "Got saFields from ssFields");
      }
      Logger.debug("", this, "", "saFields: " + saFields);

      for (int i = 0; i < saFields.length; i++) {
        Logger.debug("", this, "", "   saFields[" + i + "]: " + saFields[i]);
      }

      ProcessData[] pdaResult = pm.getProcessesData(ui, pdaHeaders);

      if (pdaResult != null && pdaResult.length > 0) {
        ProcessData process = pdaResult[0];

        List<DataElement> altmp = new ArrayList<DataElement>();
        for (int i = 0; saFields != null && i < saFields.length; i++) {
          String key = saFields[i];
          ProcessDataType type = process.getVariableDataType(key);
          if (type == null)
            continue;
          String value = null;
          Logger.debug("", this, "", "key: " + key);
          Logger.debug("", this, "", "type: " + type);
          Logger.debug("", this, "", "value: " + value);
          value = process.getFormatted(key);

          altmp.add(new DataElement(key, value, type.toString()));
        }
        Logger.debug("", this, "", "altmp: " + altmp);
        if (altmp.size() > 0) {
          retObj = new DataElementSet();
          DataElement[] ade = new DataElement[altmp.size()];
          retObj.setResult(altmp.toArray(ade));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(user, this, "getProcessData", "Error: caught exception: " + e.getMessage());
      throw new Exception("Error: caught exception: " + e.getMessage());
    }

    return retObj;
  }

  /**
   * This method is used to call the the method setProcessData from an iFlow block. Since iflow blocks only uses simple data types,
   * this method converts String Arrays to a DataElementSet and calls the setProcessData method with that data.
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id
   * @param names
   *          List of the names of the variables to be set
   * @param values
   *          List of the values to assign to the variables
   * @param types
   *          List of the variables types
   * @return "OK" if executed with success
   * @throws Exception
   */
  public String setProcessDataFromBlock(String user, String password, int flowid, int pid, int subpid, String[] names,
      String[] values, String[] types) throws Exception {
    DataElementSet desFields = null;

    if (names != null && names.length > 0) {
      desFields = new DataElementSet();
      DataElement[] result = new DataElement[names.length];
      for (int i = 0; i < names.length; i++) {
        result[i] = new DataElement(names[i], "", "String");
        if (values.length > i && values[i] != null)
          result[i].setValue(values[i]);
        if (types.length > i && types[i] != null)
          result[i].setType(types[i]);
      }
      desFields.setResult(result);
    }
    return setProcessData(user, password, flowid, pid, subpid, desFields);
  }

  /**
   * Sets the values of the process variables
   * 
   * @param user
   *          User
   * @param password
   *          User password
   * @param flowid
   *          Id of the flow
   * @param pid
   *          Process id
   * @param subpid
   *          Subprocess id
   * @param desFields
   *          Set containing the variables names, values and types, to be set the in the process
   * @return "OK" if executed with success
   * @throws Exception
   */
  public String setProcessData(String user, String password, int flowid, int pid, int subpid, DataElementSet desFields)
      throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "setProcessData", "Wrong user/password");
      throw new Exception("Wrong user/password");
    }

    if (desFields == null || desFields.getResult() == null) {
      Logger.warning(user, this, "setProcessData", "No fields found to set process dataSet variables");
      throw new Exception("No fields found to set process dataSet variables");
    }

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();
      Flow flow = BeanFactory.getFlowBean();

      ProcessHeader[] pdaHeaders = { new ProcessHeader(flowid, pid, subpid) };
      ProcessData[] pdaResult = pm.getProcessesData(ui, pdaHeaders);

      if (pdaResult != null && pdaResult.length > 0) {
        ProcessData pd = pdaResult[0];

        DataElement[] deFields = desFields.getResult();
        for (int i = 0; deFields != null && i < deFields.length; i++) {
          String key = deFields[i].getName();
          if (pd.isListVar(key))
            continue; // TODO

          ProcessSimpleVariable var = pd.get(key);
          var.parse(deFields[i].getValue());
          pd.set(var);
        }

        flow.saveDataSet(ui, pd, null);
      }

    } catch (Exception e) {
      e.printStackTrace();
      Logger.warning(user, this, "setProcessData", "Error: caught exception: " + e.getMessage());
      throw new Exception("Error: caught exception: " + e.getMessage());
    }
    return SUCCESS;
  }

}
