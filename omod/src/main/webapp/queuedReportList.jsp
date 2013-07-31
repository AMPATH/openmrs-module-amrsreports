<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.list"/>

<script type="text/javascript">
    $j(document).ready(function () {

        $j('.show_hide').showHide({
            speed: 1000,
            easing: 'swing',
            changeText: 1,
            showText: 'View All', // the button text to show when a div is closed
            hideText: 'View Last Two' // the button text to show when a div is open
        });

        $j('.remove').delegate('','click', function() {
            $j("#dlgRemoveReport").dialog("open");
             thisReport = $j(this).attr("id");
             //thisParentDiv = $j(this).parentNode;

            return false;

        });


        $j("#dlgRemoveReport" ).dialog({
            autoOpen:false,
            modal: true,
            /*show: 'slide',*/
            height: 'auto',
            hide: 'slide',
            width:600,
            cache: false,
            position: 'middle',
            buttons: {
                "Remove": function () {

                    if(thisReport !=null){
                      DWRAmrsReportService.purgeQueuedReport(thisReport,function(data){
                         if(data=="The report was successfully removed"){
                             $j("#openmrs_dwr_error_msg").html(data);
                             /*$j(".queuedReportsSubSectionContent").remove(thisParentDiv);*/
                             $j(this).dialog("close");
                             location.reload();
                         }
                         else{
                             $j("#openmrs_dwr_error_msg").html(data);
                             $j(this).dialog("close");
                         }
                      });
                    }

                },
                "Cancel": function () { $j(this).dialog("close"); }
            }
        });
    });


</script>

<%@ include file="localHeader.jsp" %>

<a href="queuedReport.form">Add a Scheduled Report</a>

<br/>


<openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                 parameters="status=NEW|title=Queued Reports"/>

<openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                 parameters="status=RUNNING|title=Running Reports"/>

<openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                 parameters="status=COMPLETE|title=Completed Reports"/>

<openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                 parameters="status=ERROR|title=Reports in Error"/>

<div id="dlgRemoveReport" title="Remove Report"><p>Are you sure you want to remove the Report?</p></div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
