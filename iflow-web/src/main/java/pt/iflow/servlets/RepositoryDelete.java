/**
 * RepositoryDelete.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
* 
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright (c) 2005 iKnow</p>
* 
* @author iKnow
* 
* @web.servlet
* name="RepositoryDelete"
* 
* @web.servlet-mapping
* url-pattern="/delete.rep"
*/
public class RepositoryDelete extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 256810223696106380L;

  public RepositoryDelete() {  }

  public void init() throws ServletException {  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String file = request.getParameter("file");
    String type = request.getParameter("type");
    
    UserInfoInterface userInfo = (UserInfoInterface)request.getSession().getAttribute(Const.USER_INFO);
    IMessages msg = userInfo.getMessages();
    
    if (file == null || file.equals("") || type == null || type.equals("") || null == userInfo) { 
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
      return;
    }
    
    if ((!userInfo.isOrgAdmin() && !userInfo.isSysAdmin())) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg.getString("admin.error.unauthorizedaccess"));
      return;
    }

    try {
      Repository rep = BeanFactory.getRepBean();
      boolean success = false;
      
      if (ResourceNavConsts.STYLESHEETS.equals(type))
        success = rep.removeStyleSheet(userInfo, file);
      else if (ResourceNavConsts.EMAIL_TEMPLATES.equals(type))
        success = rep.removeEmailTemplate(userInfo, file);
      else if (ResourceNavConsts.PRINT_TEMPLATES.equals(type))
        success = rep.removePrintTemplate(userInfo, file);
      else if (ResourceNavConsts.PUBLIC_FILES.equals(type))
        success = rep.removeWebFile(userInfo, file);

      if(success)
        request.setAttribute("actionResult", "File "+file+" was deleted");
      else 
        request.setAttribute("actionResult", "File "+file+" could not be deleted");
      
      request.getRequestDispatcher("Admin/Resources/dolist.jsp").forward(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}

