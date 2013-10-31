<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.list"/>

<script type="text/javascript">
    $j(document).ready(function(){

        $j(".interval").each(function(){
            var intervalString = getScheduleInterval($j(this).attr("seconds"));
            $j(this).text(intervalString);
        });

        $j('.show_hide').showHide({
            speed: 1000,
            easing: 'swing',
            changeText: 1,
            showText: 'View All', // the button text to show when a div is closed
            hideText: 'View Last Two' // the button text to show when a div is open
        });

    });

    function getScheduleInterval(interval){
        var units;
        var repeatInterval;

        if (interval <= 0) {
            return "";
        }
        else if (interval < 60) {
            units = "second";
            repeatInterval = interval;
        } else if (interval < 3600) {
            units = "minute";
            repeatInterval = interval / 60;
        } else if (interval < 86400) {
            units = "hour";
            repeatInterval = interval / 3600;
        } else {
            units = "day";
            repeatInterval = interval / 86400;
        }

        if (repeatInterval == 1) {
            repeatInterval = "";
        } else {
            units += "s";
        }

        return "every " + repeatInterval + " " + units;
    }

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

<%@ include file="/WEB-INF/template/footer.jsp" %>
