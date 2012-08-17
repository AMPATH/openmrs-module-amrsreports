package org.openmrs.module.amrsreport.web.dwr;

import net.sf.saxon.value.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.util.MimeConstants;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.FileCopyUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi magaja
 * Date: 6/6/12
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class DWRAmrsReportService {
    private static final Log log = LogFactory.getLog(DWRAmrsReportService.class);

    public String viewMoreDetails(String file,String id){



        //open the file and do all the manipulation
        AdministrationService as = Context.getAdministrationService();
        String folderName=as.getGlobalProperty("amrsreport.file_dir");

        File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

        File amrsFile=null;
        FileInputStream fstream = null;
        DataInputStream in =null;
        BufferedReader br=null;
        String record=null;
        String [] splitByComma=null;
        String finaldata=new String();
        List<String> recordsAfterAll=new ArrayList<String>();
        StringBuilder strColumnData = new StringBuilder();
        String columns=new String();
        String [] columnsSplit=null;
        String [] valuesArray;
        try {
            amrsFile=new File(fileDirectory,file);
            fstream = new FileInputStream(amrsFile);
            in      = new DataInputStream(fstream);
            br      = new BufferedReader(new InputStreamReader(in));
            //get the first row to be used as columns
            columns=br.readLine();
            columnsSplit=columns.split(",");
            //log.info(columnsSplit);
            while ((record = br.readLine()) != null)   {


                    splitByComma=record.split(",");

                if(stripLeadingAndTrailingQuotes(splitByComma[0]).equals(id)){
                    for (int i = 0; i < columnsSplit.length; i++) {
                        String columnName =stripLeadingAndTrailingQuotes(columnsSplit[i]);
                        String value = stripLeadingAndTrailingQuotes(splitByComma[i]);


                        strColumnData.append(columnName).append("    :").append(value).append(",");

                    }

               }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //log.info("We are returning now "+recordsAfterAll);
        log.info(strColumnData.toString());
        return strColumnData.toString();
    }
    public String viewMoreDetailsRender(String bff,String id){
        String line=new String();
        String columns=new String();
        String [] columnsSplitDetails=null;
        String [] splitByCommaDetails=null;
        StringBuilder strColumnDataDetails = new StringBuilder();


        //open the file and do all the manipulation
        AdministrationService as = Context.getAdministrationService();
        String folderName=as.getGlobalProperty("amrsreport.file_dir");

        File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

        ///
        File amrsFile=null;
        FileInputStream fstream = null;
        DataInputStream in =null;
        BufferedReader br=null;

        //log.info("lets log the buffer here "+bff);

        try {
            amrsFile=new File(fileDirectory,bff);
            fstream = new FileInputStream(amrsFile);
            in      = new DataInputStream(fstream);
            br      = new BufferedReader(new InputStreamReader(in));
            columns=br.readLine();
            columnsSplitDetails=columns.split(",");
            while (( line = br.readLine()) != null){

                splitByCommaDetails=line.split(",");

                if(stripLeadingAndTrailingQuotes(splitByCommaDetails[0]).equals(id)){
                    for (int v = 0; v < columnsSplitDetails.length; v++) {
                        String columnName =stripLeadingAndTrailingQuotes(columnsSplitDetails[v]);
                        String value = stripLeadingAndTrailingQuotes(splitByCommaDetails[v]);

                        strColumnDataDetails.append(columnName).append("    :").append(value).append(",");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return strColumnDataDetails.toString();
    }
    public void downloadCSV(String csvFile)  {
        //open the file and do all the manipulation
        HttpServletResponse httpServletResponse = null;

        AdministrationService as = Context.getAdministrationService();
        String folderName=as.getGlobalProperty("amrsreport.file_dir");
        File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
        String urlToDownLoad= fileDirectory+"/"+csvFile;


         try{
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(httpServletResponse.getOutputStream() ) );
        String mimeType = new MimetypesFileTypeMap().getContentType( urlToDownLoad );

         log.info("We should reach here "+mimeType);


        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + urlToDownLoad);
        //httpServletResponse.setContentType(MimeConstants.MIME_PLAIN);
        //httpServletResponse.setContentLength((int) urlToDownLoad.length());
             bw.write(urlToDownLoad);

        //FileCopyUtils.copy(new FileInputStream(urlToDownLoad), httpServletResponse.getOutputStream());

       bw.flush();
             bw.close();
         }
         catch (Exception e){
             e.printStackTrace();
         }
        //return urlToDownLoad;
    }
    public void downloadPDF(String file)  {
        AdministrationService as = Context.getAdministrationService();
        String folderName=as.getGlobalProperty("amrsreport.file_dir");
        File fileDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
    }
    static String stripLeadingAndTrailingQuotes(String str)
    {
        if (str.startsWith("\""))
        {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("\""))
        {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
