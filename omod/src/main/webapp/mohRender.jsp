<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreports/mohRender.form"/>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/jquery.tools.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/TableTools.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/ZeroClipboard.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/smoothness/jquery-ui-1.8.16.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools_JUI.css"/>

<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<style type="text/css">
    .tblformat tr:nth-child(odd) {
        background-color: #009d8e;
        color: #FFFFFF;
    }

    .tblformat tr:nth-child(even) {
        background-color: #d3d3d3;
        color: #000000;
    }

    .visualPadding {
        margin: 1em;
    }

        /*
    .oneThird { float: left; width: 30%; min-height: 10em; }
*/
    .newline {
        clear: both;
        display: block;
    }
</style>

<script type="text/javascript">

    var reportDate;

    $j(document).ready(function () {
        reportDate = new DatePicker("<openmrs:datePattern/>", "reportDate", { defaultDate: new Date() });
        reportDate.setDate(new Date());
    });

</script>

<%@ include file="localHeader.jsp" %>

<b class="boxHeader">Run AMRS Reports</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" name="amrsreportrenderer" action="mohRender.form">
        <fieldset class="visualPadding oneThird">
            <legend>Report Date (as of)</legend>
                <input type="text" name="reportDate" id="reportDate"/>
        </fieldset>
        <fieldset class="visualPadding oneThird">
            <legend>Location</legend>
            <c:forEach var="location" items="${locations}">
                <input type="radio" name="location" value="${location.locationId}"/> ${location.name} <br/>
            </c:forEach>
        </fieldset>
        <fieldset class="visualPadding oneThird">
            <legend>Reports</legend>
            <c:forEach var="reportName" items="${reportNames}">
                <input type="radio" name="reportName" value="${reportName}"/> ${reportName} <br/>
            </c:forEach>
        </fieldset>
        <input id="submitButton" class="visualPadding newline" type="submit" value="Queue for processing"/>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
