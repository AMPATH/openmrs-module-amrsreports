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

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/amrsreports.css" />
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<script type="text/javascript">

    var reportDate;
    var dateScheduled;

    $j(document).ready(function () {
        reportDate = new DatePicker("<openmrs:datePattern/>", "reportDate", { defaultDate: new Date() });
        reportDate.setDate(new Date());

        dateScheduled = new DatePicker("<openmrs:datePattern/>", "dateScheduled", { defaultDate: new Date() });
        dateScheduled.setDate(new Date());

        $j("#immediately").click(function(){
            if ($j("#immediately").is(":checked")) {
                $j("#dateScheduled").attr("disabled", "disabled");
            } else {
                $j("#dateScheduled").removeAttr("disabled");
            }
        });
    });

</script>

<%@ include file="localHeader.jsp" %>

<c:if test="${not empty queuedReports}">

    <b class="boxHeader">Queued Reports</b>
    <div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
        <c:forEach var="r" items="${queuedReports}">
            <div class="queued">
                ${r.reportName} for ${r.facility} as of <openmrs:formatDate date="${r.evaluationDate}" type="textbox"/>
                (run on <openmrs:formatDate date="${r.dateScheduled}" type="textbox"/>)
            </div>
        </c:forEach>
        <c:if test="${not empty queuedReports and not empty currentReport}">
            <hr />
        </c:if>
        <c:forEach var="r" items="${currentReport}">
            <div class="running">
                ${r.reportName} for ${r.facility} as of <openmrs:formatDate date="${r.evaluationDate}" type="textbox"/>
                (run on <openmrs:formatDate date="${r.dateScheduled}" type="textbox"/>)
            </div>
        </c:forEach>
    </div>
    <br />
</c:if>

<b class="boxHeader">Run AMRS Reports</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" name="amrsreportrenderer" action="mohRender.form">
        <fieldset class="visualPadding">
            <legend>Dates</legend>
                <label for="reportDate">Report Date (as of):</label>
                <input type="text" name="reportDate" id="reportDate"/> <br /> <br />
                <label for="dateScheduled">Schedule Date (run at midnight on):</label>
                <input type="text" name="dateScheduled" id="dateScheduled"/>
                    <em>or</em>
                <input type="checkbox" name="immediate" id="immediate" value="true"/> Queue Immediately
        </fieldset>
        <fieldset class="visualPadding">
            <legend>Location</legend>
            <c:forEach var="facility" items="${facilities}">
                <input type="radio" name="facility" value="${facility.facilityId}"/> ${facility.code} - ${facility.name} <br/>
            </c:forEach>
        </fieldset>
        <fieldset class="visualPadding">
            <legend>Reports</legend>
            <c:forEach var="reportName" items="${reportNames}">
                <input type="radio" name="reportName" value="${reportName}"/> ${reportName} <br/>
            </c:forEach>
        </fieldset>
        <input id="submitButton" class="visualPadding newline" type="submit" value="Queue for processing"/>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
