/*
 * @(#)MCCode.java	1.0 07/01/2006
 *
 * Copyright 2005 FSS. All Rights Reserved.
 *
 * This software is the proprietary information of FSS.
 * Use is subject to license terms.
 *
 */

import cms.common.connectionPool.SharedConnectionPool;
import com.fss.pcms.i18n.LocaleSet;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import cms.common.*;

/**
 * @author  Arul k
 * @version 1.0 07/01/06
*/
/*
 * Modified by Ramkumar.MK 04 april 2012
 * Removed unused variables and replaced deprecate method 
 * use static class to call method instead of Object
 */
public class MCCode extends HttpServlet
{
  String contextPath 			= null;
	clsLogFile moLogFile = new clsLogFile(true);

public void service(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
{
    String entry	 		    = null;
    String sql				    = null;
    String tranType                         = null;
    String mcdesc			    = null;
    String parameter		            = null;
    RequestDispatcher rd 	            = null;
    Connection conn			    = null;
    ResultSet rs			    = null;
   // PrintWriter out			    = res.getWriter();
    PreparedStatement pStmt = null;
    contextPath = req.getContextPath();
    ArrayList<String> arrErrorList = new ArrayList<String>();
    String msFlag=null;
    try { //Added for JIRA-5447
    
    moLogFile.writeToLog(this.getClass().getName(),223,"---entry---"+req.getParameter("entry"));
    if(req.getParameter("entry") != null)
    {
        if (!(req.getParameter("entry").equals(""))){
            
                if(!DataValidation.isAlphabets(req.getParameter("entry"))) {
                    arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S220.exceptionProcessingMode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
                }
                else
                {
                    entry               = req.getParameter("entry");
                }         
                
        }
    }
    
    
    moLogFile.writeToLog(this.getClass().getName(),223,"---tranType---"+req.getParameter("tranType"));
    if(req.getParameter("tranType") != null)
    if (!(req.getParameter("tranType").equals(""))){
        
            if(!DataValidation.isAlphabets(req.getParameter("tranType"))) {
                arrErrorList.add(LocaleSet.getProperty("cms.parameters.s17.exceptionProcessingMode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
            }
            else
            {
                tranType          = req.getParameter("tranType");
            }         
            
    }
    
    int miInstCode = 0, miUserPin = 0;
    
    //HttpSession session = req.getSession(true);
    HttpSession session = req.getSession(false);
    if (session==null) 
    	res.sendRedirect("/cms/InvalidSession.html");
    Integer  moIntInstitutionCode = (Integer)session.getAttribute("sessInstitutionCode") ;    
    miInstCode = moIntInstitutionCode.intValue();
      
    Integer  moIntUserPin =       (Integer)session.getAttribute("sessUserPin") ;
    miUserPin = moIntUserPin.intValue();
    
    moLogFile.writeToLog(this.getClass().getName(),223,"---InstCode---"+miInstCode);
    moLogFile.writeToLog(this.getClass().getName(),223,"---UserPIN---"+miUserPin);
        
    if(tranType.equals("ADD")) 
    {    // ADD if 01
        if(entry.equals("First")) 
        {
            if(arrErrorList.size()>0)
            {
                moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                req.setAttribute("sessStatus",arrErrorList);
                rd.forward(req,res);                
            }
            else
            {
                rd=req.getRequestDispatcher("/MCCodeRegADD.jsp?entry=First");
                rd.forward(req,res);
            }
        } 
        else if (entry.equals("Second"))
        {
            try
            {

                String mccode 	        = "";
                mcdesc	                = "";
                String ActivationStatus = "";
                msFlag="";
                
                
                moLogFile.writeToLog(this.getClass().getName(),223,"---mccode---"+req.getParameter("mccode"));
                if(req.getParameter("mccode") != null)
                {
                    if (!(req.getParameter("mccode").equals("")))
                    {  
                        if(!DataValidation.isNumeric(req.getParameter("mccode"))) {
                            arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeNumeric"));
                        }
                        else
                        {
                            mccode          = req.getParameter("mccode").trim();
                        }     
                    }
                }
                else
                    mccode = "";
                    
                moLogFile.writeToLog(this.getClass().getName(),223,"---mcdesc---"+req.getParameter("mcdesc"));
                if(req.getParameter("mcdesc") != null)
                if (!(req.getParameter("mcdesc").equals(""))){
                
                    if(!DataValidation.isAlphaNumUnscSomeSpecial(req.getParameter("mcdesc"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCodeDesc") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeAlphaNumericWithAllowableUndersocreAndSpace"));
                    }
                    else
                    {
                        mcdesc                  = req.getParameter("mcdesc").trim();
                    }         
                    
                }
                
                moLogFile.writeToLog(this.getClass().getName(),223,"---ActivationStatus---"+req.getParameter("ActivationStatus"));
                if(req.getParameter("ActivationStatus") != null)
                if (!(req.getParameter("ActivationStatus").equals(""))){
                
                    if(!DataValidation.isAlphaYesNo(req.getParameter("ActivationStatus"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.buttonActivateStatus") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
                    }
                    else
                    {
                        ActivationStatus = req.getParameter ("ActivationStatus").trim();
                    }                             
                }
                moLogFile.writeToLog(this.getClass().getName(),223,"---Money Supported Flag---"+req.getParameter("radMS"));
                if(req.getParameter("radMS") != null)
                    if (!(req.getParameter("radMS").equals(""))){
                    
                        if(!DataValidation.isAlphaYesNo(req.getParameter("radMS"))) {
                            arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.buttonMSStatus") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
                        }
                        else
                        {
                        	msFlag = req.getParameter ("radMS").trim();
                        }                             
                    }
                
                
                if(arrErrorList.size()>0)
                {
                    moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                    rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                    req.setAttribute("sessStatus",arrErrorList);
                    rd.forward(req,res);                    
                }
                else
                {
                    sql="SELECT COUNT(*) FROM MCCODE WHERE MCCODE=UPPER(?)";
                
//                    conn = SharedConnectionPool.getInstance().getConnection();
                    pStmt = conn.prepareStatement(sql);
                    pStmt.setString(1,mccode);
                    
                    int a = 0;
                    rs = pStmt.executeQuery();
                
                    if(rs.next())
                    {
                      a = rs.getInt(1);
                    }
                    CmsUtils.close(rs); //Added for JIRA-5447
                    CmsUtils.close(pStmt);  //Added for JIRA-5447
                    if( a > 0)
                    {
                        parameter="<Label  CLASS ='Label6'><b>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeAlreadyExist")+"</b></label>";
                        req.setAttribute("parameter",""+parameter);
                        rd=req.getRequestDispatcher("/MCCodeRegADD.jsp?entry=First");
                        rd.forward(req,res);
                    }
                
                    moLogFile.writeToLog(this.getClass().getName(),78,"Merc Code Check Pass");
                
                    sql="SELECT COUNT(*) FROM MCCODE WHERE MCCODEDESC=UPPER(?) and ACT_INST_CODE = ? ";
//                	Commented by Trivikram on 15-05-2012 by no need to create connection again, use create connection
//                    conn = SharedConnectionPool.getInstance().getConnection();
                    pStmt = conn.prepareStatement(sql);
                    pStmt.setString(1,mcdesc.trim());
                    pStmt.setInt(2,miInstCode);
                
                    rs = pStmt.executeQuery();
                
                    if(rs.next())
                    {
                      a = rs.getInt(1);
                    }
                    CmsUtils.close(rs);  //Added for JIRA-5447
                    CmsUtils.close(pStmt); //Added for JIRA-5447
                    if( a > 0)
                    {
                        parameter="<Label  CLASS ='label6'><b>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeDescriptionAlreadyExist")+"</b></label>";
                        req.setAttribute("parameter",""+parameter);
                        rd=req.getRequestDispatcher("/MCCodeRegADD.jsp?entry=First");
                        rd.forward(req,res);    
                    } 
                    else 
                    {
                        moLogFile.writeToLog(this.getClass().getName(),78,"Merc Code/Catg Check Pass");
                       /** if(mccode.length() < 4)
                        {
                            String pad="0000";
                            mccode=pad.substring(0,(4-mccode.length()))+mccode;
                        }
    **/
                     // sql="insert into MCCODE values(?,upper(?),upper(?),SYSDATE,?,?,SYSDATE, ?)";
                      sql="insert into MCCODE(MCCODE,MCCODEDESC,ACTIVATIONSTATUS,ACT_LUPD_DATE,ACT_INST_CODE,ACT_LUPD_USER,ACT_INS_DATE,ACT_INS_USER,MS_MCC_FLAG) values(?,upper(?),upper(?),SYSDATE,?,?,SYSDATE,?,?)";
                      moLogFile.writeToLog(this.getClass().getName(),98,"CHKPT : ------> 1");
                      pStmt = conn.prepareStatement(sql);
                      pStmt.setString(1,mccode);
                      pStmt.setString(2,mcdesc);
                      pStmt.setString(3,ActivationStatus);
                      pStmt.setInt(4,miInstCode);
                      pStmt.setInt(5,miUserPin);
                      pStmt.setInt(6,miUserPin);
                      pStmt.setString(7,msFlag);
                      
                      int insert  = pStmt.executeUpdate();
                      moLogFile.writeToLog(this.getClass().getName(),98,"CHKPT : ------> 1.1");
                      CmsUtils.close(rs);  //Added for JIRA-5447
                      CmsUtils.close(pStmt); //Added for JIRA-5447
                      if(insert == 1)
                      {
                        parameter ="<Label  CLASS ='label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusAddedSuccessfully")+"</label>";
                        req.setAttribute("parameter",""+parameter);
                        req.setAttribute("ActivationStatus",""+ActivationStatus);
                        req.setAttribute("mccode",""+mccode);
                        req.setAttribute("mcdesc",""+mcdesc);
                        req.setAttribute("radMS",""+msFlag);
                        rd=req.getRequestDispatcher("/MCCodeRegADD.jsp?entry=Second");
                        rd.forward(req,res);  
                      } 
                      else 
                      {
                        req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                        req.setAttribute("status","<Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeRegistrationFailed")+"</label>");
                        rd=req.getRequestDispatcher("/Status.jsp");
                        rd.forward(req,res); 
                      }
                    }
                }
            }
            catch(Exception e)
            {    
                moLogFile.writeToLog(this.getClass().getName(), 362, "Exception :--------> " + e.getMessage());
                clsLogFile.printStackTrace(e);
                
                String msoraErrMsg = "";
                try
                
                {
                        //pConn.rollback();
                        msoraErrMsg = Masters.getOraErrorMsg(miInstCode,e);
                }
                catch(Exception ed)
                {
                
                }
                
                req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                
                if(msoraErrMsg.length() > 0) {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+msoraErrMsg+"</label>");
                }
                else {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeRegistrationFailed")+"</label>");
                }
                    
                rd=req.getRequestDispatcher("/Status.jsp");
                rd.forward(req,res);
            }
            finally
            {
                try
                {
                  /*if(pStmt != null)
                    pStmt.close();
                    
                  if(rs != null)
                    rs.close();
                    
                  if(conn != null)
                    SharedConnectionPool.getInstance().free(conn);
                  */
                	CmsUtils.close(rs);  //Added for JIRA-5447
                    CmsUtils.close(pStmt); //Added for JIRA-5447
                    CmsUtils.close(conn); //Added for JIRA-5447
                }
                catch(Exception e)
                {
                  moLogFile.writeToLog(this.getClass().getName(),155,"Exception :------> "+e.getMessage());
                  clsLogFile.printStackTrace(e);
                }
            }
        }
    } 
    else 	if(tranType.equals("EDIT")) 
    { //add if end  edit if 02
			if(entry.equals("First")) 
      {

					rd=req.getRequestDispatcher("/MCCodeRegEdit.jsp");
				 	rd.forward(req,res);
			} 
      else if (entry.equals("Second"))
      {
        try
        {
            String linked 	= "Y";
            String mccode	= "";
            //SqlStatement  db = new SqlStatement();
            
            moLogFile.writeToLog(this.getClass().getName(),223,"---mccode---"+req.getParameter("mccode"));
            if(req.getParameter("mccode") != null)
            {
                if (!(req.getParameter("mccode").equals("")))
                {  
                    if(!DataValidation.isNumeric(req.getParameter("mccode"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeNumeric"));
                    }
                    else
                    {
                        mccode  = req.getParameter("mccode").trim();
                    }     
                }
            }
            
            if(arrErrorList.size()>0)
            {
                moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                req.setAttribute("sessStatus",arrErrorList);
                rd.forward(req,res);                    
            }
            else
            {
                
                //if(mccode.length() ==1) mccode="0"+mccode;
            	moLogFile.writeToLog(this.getClass().getName(),122,"---mccode-------" + mccode);
               // String mccdesc=db.getValue("MCCODEDESC","MCCODE","MCCODE",mccode);
               // String ActivationStatus=db.getValue("ACTIVATIONSTATUS","MCCODE","MCCODE",mccode);
            	
                //  SqlStatement  db = new SqlStatement();
                // String mccdesc=db.getValue("MCCODEDESC","MCCODE","MCCODE",mccode);
               //  String ActivationStatus=db.getValue("ACTIVATIONSTATUS","MCCODE","MCCODE",mccode);
                 String mccdesc=null;
                 String ActivationStatus=null;
                 String viewQuery="select MCCODEDESC,ACTIVATIONSTATUS,MS_MCC_FLAG from MCCODE where MCCODE=? and ACT_INST_CODE=?";            
                 String msStatus=null;
                 PreparedStatement psStmt=null ;
                 ResultSet rSet=null;
                 try{
//                 conn = SharedConnectionPool.getInstance().getConnection(); 
                 psStmt = conn.prepareStatement(viewQuery);
                 psStmt.setString(1,mccode);
                 psStmt.setInt(2,miInstCode);
                 rSet=psStmt.executeQuery();
                 if(rSet.next())
                 {
                 	mccdesc=rSet.getString(1);
                 	ActivationStatus=rSet.getString(2);
                 	msStatus=rSet.getString(3);
                 
                 }
                 CmsUtils.close(rSet);  //Added for JIRA-5447
                 CmsUtils.close(psStmt); //Added for JIRA-5447
                 }
                 catch(Exception e){
                	 moLogFile.writeToLog(this.getClass().getName(),246,"Exception :------> "+e.getMessage());
                     clsLogFile.printStackTrace(e);
                     throw new Exception(e); 
                 }
                 finally{
                	 try
                     {
                        /* if(rSet != null)
                        	 rSet.close();
                         
                         if(conn != null)
                         SharedConnectionPool.getInstance().free(conn);
                         */
                		 CmsUtils.close(rSet);  //Added for JIRA-5447
                         CmsUtils.close(psStmt); //Added for JIRA-5447
                         CmsUtils.close(conn); //Added for JIRA-5447
                     }
                     catch(Exception e)
                     {
                         moLogFile.writeToLog(this.getClass().getName(),278,"Exception while Closing Conn :------> "+e.getMessage());
                         clsLogFile.printStackTrace(e);
                         throw new Exception(e); 
                     }
                	 
                 }
                 
               // if(mccdesc.equals("Not Found") || ActivationStatus.equals("Not Found"))
                 if(mccdesc == null || ActivationStatus == null ||msStatus == null )
                 {
                    req.setAttribute("ERROR","error");
                    rd=req.getRequestDispatcher("/MCModification.jsp");
                    rd.forward(req,res);
                } 
                
              /*  if(mccdesc.equals("Error") || ActivationStatus.equals("Error") )
                {
                    req.setAttribute("ERROR","error");
                    rd=req.getRequestDispatcher("/MCModification.jsp");
                    rd.forward(req,res);
                } */
                else 
                {							
                   // String 	 Linked	= "Y";
                    try 
                    {
                        sql = " SELECT COUNT(*) "+
                              " FROM MCCODE_GROUP A, MCCODE B  "+
                              " WHERE B.MCCODE = A.MCCODE "+
                              " AND  B.MCCODE =  ? "+
                              " AND ACT_INST_CODE = ? ";
                        
                        moLogFile.writeToLog(this.getClass().getName(),226,"mccdesc :---->"+mccdesc);
                        moLogFile.writeToLog(this.getClass().getName(),226,"ActivationStatus :---->"+ActivationStatus);
                        moLogFile.writeToLog(this.getClass().getName(),226,"miInstCode :---->"+miInstCode);
                        moLogFile.writeToLog(this.getClass().getName(),226,"CHKPT :----> 2");
//                        conn = SharedConnectionPool.getInstance().getConnection();      
                        pStmt = conn.prepareStatement(sql);
                        pStmt.setString(1,mccode);
                        pStmt.setInt(2,miInstCode);
                        
                        rs  = pStmt.executeQuery();
                        moLogFile.writeToLog(this.getClass().getName(),226,"CHKPT :----> 2.1");
                        
                        if(rs.next())
                        {
                          int tl=rs.getInt(1); 
                          CmsUtils.close(rs);  //Added for JIRA-5447
                          CmsUtils.close(pStmt); //Added for JIRA-5447
                          if(tl>0)
                          {
                            linked="N";
                          }
                        }                
                        moLogFile.writeToLog(this.getClass().getName(),226,"CHKPT :----> 2.2");
                        req.setAttribute("linked",linked);
                        req.setAttribute("mccode",mccode);
                        req.setAttribute("mcdesc",mccdesc);
                        req.setAttribute("ActivationStatus",ActivationStatus);
                        req.setAttribute("msFlag",msStatus);
                        req.setAttribute("msFlagDis","N");
                        rd=getServletContext().getRequestDispatcher("/MCCodeRegEdit.jsp?entry=Second");
                        rd.forward(req,res);
                    } 
                    catch(Exception e)
                    {
                        moLogFile.writeToLog(this.getClass().getName(),246,"Exception :------> "+e.getMessage());
                        clsLogFile.printStackTrace(e);
                        throw new Exception(e);
                    }
                }
            }
        }
        catch(Exception e)
        {    
            moLogFile.writeToLog(this.getClass().getName(), 362, "Exception :--------> " + e.getMessage());
            clsLogFile.printStackTrace(e);
            
            String msoraErrMsg = "";
                try
                
                {
                        //pConn.rollback();
                        msoraErrMsg = Masters.getOraErrorMsg(miInstCode,e);
                }
                catch(Exception ed)
                {
                	 clsLogFile.printStackTrace(ed);
                }
                
                clsLogFile.printStackTrace(e);
                req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                
                if(msoraErrMsg.length() > 0) {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+msoraErrMsg+"</label>");
                }
                else {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeRegistrationFailed")+"</label>");
                }
                
            rd=req.getRequestDispatcher("/Status.jsp");
            rd.forward(req,res);
        }
        finally
        {
            try
            {
                /*if(rs != null)
                rs.close();
                
                if(conn != null)
                SharedConnectionPool.getInstance().free(conn);
                */
            	CmsUtils.close(rs);  //Added for JIRA-5447
                CmsUtils.close(pStmt); //Added for JIRA-5447
                CmsUtils.close(conn); //Added for JIRA-5447
            }
            catch(Exception e)
            {
                moLogFile.writeToLog(this.getClass().getName(),278,"Exception while Closing Conn :------> "+e.getMessage());
                clsLogFile.printStackTrace(e);
            }
        }
    } 
    else if (entry.equals("Third"))
    {
        try
        {
            String mccode	 	= "";
            String mccdesc   		= "";
            String ActivationStatus 	= "";
            String msFlg="";
            
            moLogFile.writeToLog(this.getClass().getName(),223,"---mccode---"+req.getParameter("mccode"));
            if(req.getParameter("mccode") != null)
            {
                if (!(req.getParameter("mccode").equals("")))
                {  
                    if(!DataValidation.isNumeric(req.getParameter("mccode"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeNumeric"));
                    }
                    else
                    {
                        mccode          = req.getParameter("mccode").trim();
                    }     
                }
            }
            else
                mccode = "";
                
            moLogFile.writeToLog(this.getClass().getName(),223,"---mcdesc---"+req.getParameter("mcdesc"));
            if(req.getParameter("mcdesc") != null)
            if (!(req.getParameter("mcdesc").equals(""))){
            
                if(!DataValidation.isAlphaNumUnscSomeSpecial(req.getParameter("mcdesc"))) {
                    arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCodeDesc") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeAlphaNumericWithAllowableUndersocreAndSpace"));
                }
                else
                {
                    mcdesc                  = req.getParameter("mcdesc").trim();
                }         
                
            }
            
            moLogFile.writeToLog(this.getClass().getName(),223,"---ActivationStatus---"+req.getParameter("ActivationStatus"));
            if(req.getParameter("ActivationStatus") != null)
            if (!(req.getParameter("ActivationStatus").equals(""))){
            
                if(!DataValidation.isAlphaYesNo(req.getParameter("ActivationStatus"))) {
                    arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.buttonActivateStatus") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
                }
                else
                {
                    ActivationStatus = req.getParameter ("ActivationStatus").trim();
                }                             
            }
            
            moLogFile.writeToLog(this.getClass().getName(),223,"---Money Supported Flag---"+req.getParameter("radMS"));
            if(req.getParameter("radMS") != null)
                if (!(req.getParameter("radMS").equals(""))){
                
                    if(!DataValidation.isAlphaYesNo(req.getParameter("radMS"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.buttonMSStatus") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionIsInvalid"));
                    }
                    else
                    {
                    	msFlg = req.getParameter ("radMS").trim();
                    }                             
                }
            if(arrErrorList.size()>0)
            {
                moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                req.setAttribute("sessStatus",arrErrorList);
                rd.forward(req,res);                    
            }
            else
            {
                //SqlStatement  db = new SqlStatement();
                
                sql="SELECT * FROM MCCODE WHERE MCCODEDESC=UPPER(?) and MCCODE != ? and ACT_INST_CODE = ? ";
                
                moLogFile.writeToLog(this.getClass().getName(),295,"CHKPT :-----> 3");
//                conn = SharedConnectionPool.getInstance().getConnection();
                pStmt = conn.prepareStatement(sql);
                pStmt.setString(1,mcdesc);
                pStmt.setString(2,mccode);
                pStmt.setInt(3,miInstCode);
                
                int count= pStmt.executeUpdate();
                moLogFile.writeToLog(this.getClass().getName(),295,"CHKPT :-----> 3.1");
                
                CmsUtils.close(pStmt); //Added for JIRA-5447
                if( count > 0) 
                {
                    parameter="<Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeAlreadyExist")+"</label>";
                    req.setAttribute("mccode",mccode);
                    req.setAttribute("mcdesc",mccdesc);
                    req.setAttribute("ActivationStatus",ActivationStatus);
                    req.setAttribute("parameter",""+parameter);
                    req.setAttribute("linked","Y");
                    rd=req.getRequestDispatcher("/MCCodeRegEdit.jsp?entry=Second");

                    rd.forward(req,res);
                }
                else{
                sql=" update  mccode set mccodedesc=upper(?),MS_MCC_FLAG=?, "+            //ACTIVATIONSTATUS=UPPER(?), "+
                    " ACT_LUPD_DATE = SYSDATE, ACT_LUPD_USER = ? where mccode=? and ACT_INST_CODE = ? ";
                
                moLogFile.writeToLog(this.getClass().getName(),295,"CHKPT :-----> 3.2");
                pStmt = conn.prepareStatement(sql);
                pStmt.setString(1,mcdesc);
                //pStmt.setString(2,ActivationStatus);
                pStmt.setString(2,msFlg);
                pStmt.setInt(3,miUserPin);
                pStmt.setString(4,mccode);
                pStmt.setInt(5,miInstCode);
                
                int insert  = pStmt.executeUpdate();
                moLogFile.writeToLog(this.getClass().getName(),295,"CHKPT :-----> 3.3");
                CmsUtils.close(pStmt); //Added for JIRA-5447
                if(insert == 1)
                {
                    parameter ="<center><Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode")+" "+ mccode + " "+LocaleSet.getProperty("cms.PreAuth.S322.statusModifiedSuccessfully")+"</label></center>";
                    req.setAttribute("parameter",""+parameter);
                    req.setAttribute("mccode",""+mccode);
                    req.setAttribute("mcdesc",""+mcdesc);
                    req.setAttribute("linked","N");
                    req.setAttribute("ActivationStatus",""+ActivationStatus);
                    req.setAttribute("msFlag",""+msFlg);
                    req.setAttribute("msFlagDis","Y");
                    rd=req.getRequestDispatcher("/MCCodeRegEdit.jsp?entry=First");
                    rd.forward(req,res);
                } 
                else 
                {
                    parameter="<center><Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeModificationFailed")+"</label></center>";
                    req.setAttribute("parameter",parameter);
                    req.setAttribute("mccode",mccode);
                    req.setAttribute("mcdesc",mcdesc);
                    req.setAttribute("ActivationStatus",ActivationStatus);
                    rd=req.getRequestDispatcher("/MCCodeRegADD.jsp?entry=Second");//MCCodeRegEdit.jsp?entry=Third");
                    rd.forward(req,res);
                }
              }  
            }
        }
        catch(Exception e)
        {    
            moLogFile.writeToLog(this.getClass().getName(), 362, "Exception :--------> " + e.getMessage());
            clsLogFile.printStackTrace(e);
            
            String msoraErrMsg = "";
                try
                
                {
                        //pConn.rollback();
                        msoraErrMsg = Masters.getOraErrorMsg(miInstCode,e);
                }
                catch(Exception ed)
                {
                    moLogFile.writeToLog(this.getClass().getName(), 362, "Exception From Dynamic Error :--------> " + ed.getMessage());
                }                
                
                req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                
                if(msoraErrMsg.length() > 0) {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+msoraErrMsg+"</label>");
                }
                else {
                    req.setAttribute("status","<Label  CLASS ='Label6'>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeModificationFailed")+"</label>");
                }
                
            rd=req.getRequestDispatcher("/Status.jsp");
            rd.forward(req,res);
        }
        finally
        {
            try
            {
            /*if(pStmt != null)
                pStmt.close();
            
            if(conn != null)
                SharedConnectionPool.getInstance().free(conn);
            */
            CmsUtils.close(rs);  //Added for JIRA-5447
            CmsUtils.close(pStmt); //Added for JIRA-5447
            CmsUtils.close(conn);  //Added for JIRA-5447
            }
            catch(Exception e)
            {
                moLogFile.writeToLog(this.getClass().getName(),371,"Exception while closing Conn :--->"+e.getMessage());
                clsLogFile.printStackTrace(e);
            }
        }
    }
} 
    else 	if(tranType.equals("DELETE")) 
    { //edit if end   delete if 03
        if(entry.equals("First")) 
        {
            rd=req.getRequestDispatcher("/MCCodeRegDelete.jsp");
            rd.forward(req,res);
        } 
      else if (entry.equals("Second"))
      {
        try
        {
            String mccode	= "";
            
            moLogFile.writeToLog(this.getClass().getName(),223,"---mccode---"+req.getParameter("mccode"));
            if(req.getParameter("mccode") != null)
            {
                if (!(req.getParameter("mccode").equals("")))
                {  
                    if(!DataValidation.isNumeric(req.getParameter("mccode"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeNumeric"));
                    }
                    else
                    {
                        mccode  = req.getParameter("mccode").trim();
                    }     
                }
            }
            
            
            if(arrErrorList.size()>0)
            {
                moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                req.setAttribute("sessStatus",arrErrorList);
                rd.forward(req,res);                    
            }
            else
            {
               // SqlStatement  db = new SqlStatement();
    
               // String mccdesc=db.getValue("MCCODEDESC","MCCODE","MCCODE",mccode);
               // String ActivationStatus=db.getValue("ACTIVATIONSTATUS","MCCODE","MCCODE",mccode);
            	  
            	String mccdesc=null;
                String ActivationStatus=null;
                String viewQuery="select MCCODEDESC,ACTIVATIONSTATUS,MS_MCC_FLAG from MCCODE where MCCODE=? and ACT_INST_CODE=?";            
                String msStatus=null;
//                conn = SharedConnectionPool.getInstance().getConnection(); 
                pStmt = conn.prepareStatement(viewQuery);
                pStmt.setString(1,mccode);
                pStmt.setInt(2,miInstCode);
                rs=pStmt.executeQuery();
                if(rs.next())
                {
                	mccdesc=rs.getNString(1);
                	ActivationStatus=rs.getNString(2);
                	msStatus=rs.getNString(3);
                
                }
                CmsUtils.close(rs);  //Added for JIRA-5447
                CmsUtils.close(pStmt); //Added for JIRA-5447

               // if(mccdesc.equals("Not Found") || ActivationStatus.equals("Not Found"))
                if(mccdesc == null || ActivationStatus == null ||msStatus == null )
                {
                    req.setAttribute("ERROR","error");
                    rd=req.getRequestDispatcher("/MCModification.jsp");
                    rd.forward(req,res);
                } 
    
               /* if(mccdesc.equals("Error") || ActivationStatus.equals("Error") )
                {
                    req.setAttribute("ERROR","error");
                    rd=req.getRequestDispatcher("/MCModification.jsp");
                    rd.forward(req,res);
                } */
                else 
                {
                    req.setAttribute("mccode",mccode);
                    req.setAttribute("mcdesc",mccdesc);
                    req.setAttribute("ActivationStatus",""+ActivationStatus);
                    req.setAttribute("msFlag",msStatus);
                    rd=req.getRequestDispatcher("/MCCodeRegDelete.jsp?entry=Second");
                    rd.forward(req,res);
                }
            }
        }
        catch(Exception e)
        {    
            moLogFile.writeToLog(this.getClass().getName(), 362, "Exception :--------> " + e.getMessage());
            clsLogFile.printStackTrace(e);
            
            String msoraErrMsg = "";
                try
                
                {
                        //pConn.rollback();
                        msoraErrMsg = Masters.getOraErrorMsg(miInstCode,e);
                }
                catch(Exception ed)
                {
                    moLogFile.writeToLog(this.getClass().getName(), 362, "Exception Ora Dyna Message :--------> " + ed.getMessage());
                }
                
                req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                
                if(msoraErrMsg.length() > 0) {
                    req.setAttribute("status","<label class=label6>"+msoraErrMsg+"</label>");
                }
                else {
                    req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantcategoryCodedoesntexists")+"</label>");
                }
                                
            rd=req.getRequestDispatcher("/Status.jsp");
            rd.forward(req,res);
        }
    }
    else if (entry.equals("Third"))
    {
        try
        {
            String mccode	    = "";
            String mccdesc    = "";
            String linked     = "Y";

            moLogFile.writeToLog(this.getClass().getName(),223,"---mccode---"+req.getParameter("mccode"));
            if(req.getParameter("mccode") != null)
            {
                if (!(req.getParameter("mccode").equals("")))
                {  
                    if(!DataValidation.isNumeric(req.getParameter("mccode"))) {
                        arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCode") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeNumeric"));
                    }
                    else
                    {
                        mccode      = req.getParameter("mccode").trim();
                    }     
                }
            }
            else
                mccode = "";
                
            moLogFile.writeToLog(this.getClass().getName(),223,"---mcdesc---"+req.getParameter("mcdesc"));
            if(req.getParameter("mcdesc") != null)
            if (!(req.getParameter("mcdesc").equals(""))){
            
                if(!DataValidation.isAlphaNumUnscSomeSpecial(req.getParameter("mcdesc"))) {
                    arrErrorList.add(LocaleSet.getProperty("cms.PreAuth.S66.textMercCatCodeDesc") + " " + LocaleSet.getProperty("cms.parameters.s15.exceptionMustBeAlphaNumericWithAllowableUndersocreAndSpace"));
                }
                else
                {
                    mccdesc    = req.getParameter("mcdesc").trim();
                }         
                
            }
            
            
            if(arrErrorList.size()>0)
            {
                moLogFile.writeToLog(this.getClass().getName(),122,"---Inside ErrorList Check---");
                rd = req.getRequestDispatcher("/ErrorValidation.jsp");    //request.getRequestDispatcher("/ErrorValidation.jsp");
                req.setAttribute("sessStatus",arrErrorList);
                rd.forward(req,res);                    
            }
            else
            {
                //SqlStatement  db = new SqlStatement();
                
                sql = " SELECT COUNT(*) "+
                      " FROM MCCODE_GROUP A, MCCODE B  "+
                      " WHERE B.MCCODE = A.MCCODE "+
                      " AND  B.MCCODE =  ? "+
                      " AND B.ACT_INST_CODE = ? ";
                    
                moLogFile.writeToLog(this.getClass().getName(),226,"CHKPT :----> 0.1");
//                conn = SharedConnectionPool.getInstance().getConnection();      
                pStmt = conn.prepareStatement(sql);
                pStmt.setString(1,mccode);
                pStmt.setInt(2,miInstCode);
                    
                rs  = pStmt.executeQuery();
                moLogFile.writeToLog(this.getClass().getName(),226,"CHKPT :----> 0.2");
                
                if(rs.next())
                {
                  int tl=rs.getInt(1);  
                  CmsUtils.close(rs);  //Added for JIRA-5447
                  CmsUtils.close(pStmt); //Added for JIRA-5447
                  if(tl>0)
                  {
                    moLogFile.writeToLog(this.getClass().getName(),449,"Merc Linked");
                    req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                    req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusDeleteFailedMerchantCategoryCodeislinked")+"</label>");
                    rd=getServletContext().getRequestDispatcher("/Status.jsp");
                    rd.forward(req,res);
                  }
                  else
                  {
                    sql="delete mccode where mccode = ? and ACT_INST_CODE = ? ";
                
                    moLogFile.writeToLog(this.getClass().getName(),437,"CHAKPT :------> 4");
//                    conn  = SharedConnectionPool.getInstance().getConnection();
                    pStmt = conn.prepareStatement(sql);
                    pStmt.setString(1,mccode);
                    pStmt.setInt(2,miInstCode);
        
                    int delete  = pStmt.executeUpdate();
                    
                    moLogFile.writeToLog(this.getClass().getName(),437,"CHAKPT :------> 4.1");
                    CmsUtils.close(rs);  //Added for JIRA-5447
                    CmsUtils.close(pStmt); //Added for JIRA-5447
                   if(delete==2292)
                   {
                        moLogFile.writeToLog(this.getClass().getName(),437,"CHAKPT :------> 4.2");
                        req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                        req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode")+" "+mccode+" "+LocaleSet.getProperty("cms.PreAuth.S322.statusDeleteFailed")+"</label>");
                        rd=req.getRequestDispatcher("/Status.jsp");
                        rd.forward(req,res);
      
                    } 
                    else if(delete < 0)	
                    {
                        moLogFile.writeToLog(this.getClass().getName(),437,"CHAKPT :------> 4.3");
                        req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                        req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode")+" "+mccode+" "+LocaleSet.getProperty("cms.PreAuth.S322.statusDeleteFailed")+"</label>");
                        rd=req.getRequestDispatcher("/Status.jsp");
                        rd.forward(req,res);
      
                    } 
                    else if(delete==1)
                    {
                          req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                          req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode")+" "+mccode+" "+LocaleSet.getProperty("cms.PreAuth.S322.statusDeletedSuccessfully")+"</label>");
                          rd=req.getRequestDispatcher("/Status.jsp");
                          rd.forward(req,res);
                    }
                  }
                }
            }
        }
        catch(Exception e)
        {    
            moLogFile.writeToLog(this.getClass().getName(), 362, "Exception :--------> " + e.getMessage());
            clsLogFile.printStackTrace(e);
            
            String msoraErrMsg = "";
                try
                
                {
                        //pConn.rollback();
                        msoraErrMsg = Masters.getOraErrorMsg(miInstCode,e);
                }
                catch(Exception ed)
                {
                    moLogFile.writeToLog(this.getClass().getName(), 362, "Exception Ora Dyna Message :--------> " + e.getMessage());
                }
                
                req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
                
                if(msoraErrMsg.length() > 0) {
                    req.setAttribute("status","<label class=label6>"+msoraErrMsg+"</label>");
                }
                else {
                    req.setAttribute("status","<label class=label6>"+LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeDeleteFailed")+"</label>");
                }
                
            rd=req.getRequestDispatcher("/Status.jsp");
            rd.forward(req,res);
        }
        finally
        {
            try
            {
             /* if(pStmt != null)
                pStmt.close();
             
              if(rs != null)
                rs.close();
                
              if(conn != null)
                SharedConnectionPool.getInstance().free(conn);
              */
            	CmsUtils.close(rs);  //Added for JIRA-5447
                CmsUtils.close(pStmt); //Added for JIRA-5447
                CmsUtils.close(conn); //Added for JIRA-5447
            }
            catch(Exception e)
            {
              moLogFile.writeToLog(this.getClass().getName(),490,"Exception while closing CONN :------> "+e.getMessage());
              clsLogFile.printStackTrace(e);
            }
        }
    }
} else 	if(tranType.equals("VIEW")) 
{ //delete if end view if 04
    if(entry.equals("First")) 
    {
        //out.println("First");
        rd=req.getRequestDispatcher("/MCCodeRegView.jsp");
        rd.forward(req,res);
    } 
    else if (entry.equals("Second"))
    {
        try
        {
            String mccode	= req.getParameter("mccode").trim();
          //  SqlStatement  db = new SqlStatement();
           // String mccdesc=db.getValue("MCCODEDESC","MCCODE","MCCODE",mccode);
          //  String ActivationStatus=db.getValue("ACTIVATIONSTATUS","MCCODE","MCCODE",mccode);
            String mccdesc=null;
            String ActivationStatus=null;
            String viewQuery="select MCCODEDESC,ACTIVATIONSTATUS,MS_MCC_FLAG from MCCODE where MCCODE=? and ACT_INST_CODE=?";            
            String msStatus=null;
//            conn = SharedConnectionPool.getInstance().getConnection(); 
            pStmt = conn.prepareStatement(viewQuery);
            pStmt.setString(1,mccode);
            pStmt.setInt(2,miInstCode);
            rs=pStmt.executeQuery();
            if(rs.next())
            {
            	mccdesc=rs.getNString(1);
            	ActivationStatus=rs.getNString(2);
            	msStatus=rs.getNString(3);
            
            }
            CmsUtils.close(rs);  //Added for JIRA-5447
            CmsUtils.close(pStmt); //Added for JIRA-5447
          //  if(mccdesc.equals("Not Found") || ActivationStatus.equals("Not Found"))
            if(mccdesc == null || ActivationStatus == null ||msStatus == null )
            {
                req.setAttribute("ERROR","error");
                rd=req.getRequestDispatcher("/MCModification.jsp");
                rd.forward(req,res);
            } 
            
          /*  if(mccdesc.equals("Error") || ActivationStatus.equals("Error") )
            {
                req.setAttribute("ERROR","error");
                rd=req.getRequestDispatcher("/MCModification.jsp");
                rd.forward(req,res);
            } */
            else 
            {
                req.setAttribute("mccode",mccode);
                req.setAttribute("mcdesc",mccdesc);
                req.setAttribute("ActivationStatus",""+ActivationStatus);
                req.setAttribute("msFlag",""+msStatus);
                rd=req.getRequestDispatcher("/MCCodeRegView.jsp?entry=Second");
                rd.forward(req,res);
            }            
        }
        catch(Exception e)	
        {
            moLogFile.writeToLog(this.getClass().getName(),540,"Exception :-------> "+e.getMessage());
            clsLogFile.printStackTrace(e);
            req.setAttribute("heading",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCode"));
            req.setAttribute("status",LocaleSet.getProperty("cms.PreAuth.S322.statusMerchantCategoryCodeViewFailed"));
            rd=req.getRequestDispatcher("/Status.jsp");
            //rd.forward(req,res);
        }
        finally
        {
            try
            {
             /* if(pStmt != null)
                pStmt.close();
             
              if(rs != null)
            	  rs.close();
                
              if(conn != null)
                SharedConnectionPool.getInstance().free(conn);
              */
            	CmsUtils.close(rs);  //Added for JIRA-5447
                CmsUtils.close(pStmt); //Added for JIRA-5447
                CmsUtils.close(conn); //Added for JIRA-5447
            }
            catch(Exception e)
            {
              moLogFile.writeToLog(this.getClass().getName(),490,"Exception while closing CONN :------> "+e.getMessage());
              clsLogFile.printStackTrace(e);
            }
        }
    }
   } //View if end
 } catch (Exception e) {
    	moLogFile.writeToLog(this.getClass().getName(),540,"Exception :-------> "+e.getMessage());
        clsLogFile.printStackTrace(e);	
    }finally {
    		try {
    			CmsUtils.close(rs);
    			CmsUtils.close(pStmt);
    			CmsUtils.close(conn);
    		} catch(Exception e)
    		{
    		moLogFile.writeToLog(this.getClass().getName(),540,"Exception in closing conection:-------> "+e.getMessage());
    		clsLogFile.printStackTrace(e);
			}
    }
}
}