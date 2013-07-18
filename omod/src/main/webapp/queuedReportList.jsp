<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/diQuery-collapsiblePanel.js"/>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.list"/>

<script type="text/javascript">
    $j(document).ready(function () {

        $j('.show_hide').showHide({
            speed: 500,  // speed you want the toggle to happen
            // easing: '',  // the animation effect you want. Remove this line if you dont want an effect and if you haven't included jQuery UI
            changeText: 1, // if you dont want the button text to change, set this to 0
            showText: 'See All', // the button text to show when a div is closed
            hideText: 'Top Two' // the button text to show when a div is open
        });
    });
</script>

<style>
    .subheading {
        background-color: #ccc;
        line-height: 2em;
        font-size: 1.2em;
        margin-top: 1em;
    }

    .moreLink a {
        padding-left: 1em;
        font-style: italic;
    }

    .groupHeader {
        border-style: solid;
        border-color: black;
        border-width: 1px 0;
        line-height: 1.75em;
    }

    .groupContent {
        margin: 0.5em 0;
    }

    .actions {
        display: inline-block;
        width: 10em;
        text-align: center;
    }

    .reportName {
        display: inline-block;
        width: 15em;
        text-align: center;
    }

    .evaluationDate {
        display: inline-block;
        width: 10em;
    }

    .scheduledDate {
        display: inline-block;
        width: 15em;
    }

    #wrapper {
        margin: 0 1em;
    }

    .extraContent {
        display: none;
    }
</style>

<%@ include file="localHeader.jsp" %>

<a href="queuedReport.form">Add a Scheduled Report</a>

<br/>
<br/>

<b class="boxHeader">View AMRS Reports</b>

<div class="box">

    <div id="wrapper">

        <div class="subheading">
            Queued Reports
        </div>

        <openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                         parameters="status=NEW"/>

        <div class="subheading">
            Running Reports
        </div>

        <openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                         parameters="status=RUNNING"/>

        <div class="subheading">
            Completed Reports
        </div>

        <openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                         parameters="status=COMPLETE"/>

        <div class="subheading">
            Reports in Error
        </div>

        <openmrs:portlet id="queuedAMRSReports" moduleId="amrsreports" url="queuedAMRSReports"
                         parameters="status=ERROR"/>
    </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
