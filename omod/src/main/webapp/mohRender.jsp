<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreports/mohRender.form"/>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery.tools.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/TableTools.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/ZeroClipboard.js"/>

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/smoothness/jquery-ui-1.8.16.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools_JUI.css"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/amrsreports.css" />

<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/datetimepicker.js"/>

<script type="text/javascript">

    var reportDate;
    var dateScheduled;

    $j(document).ready(function () {
        reportDate = new DatePicker("<openmrs:datePattern/>", "reportDate", { defaultDate: new Date() });
        reportDate.setDate(new Date());

        $j("#immediately").click(function(){
            if ($j("#immediately").is(":checked")) {
                $j("#dateScheduled").attr("disabled", "disabled");
            } else {
                $j("#dateScheduled").removeAttr("disabled");
            }
        });

        $j("#repeatSchedule").click(function(){
            if ($j("#repeatSchedule").is(":checked")) {
                $j("#repeatIntervalUnits").removeAttr("disabled");
                $j("#repeatInterval").removeAttr("disabled");
            } else {
                $j("#repeatInterval").val("");
                $j("#repeatIntervalUnits").attr("disabled", "disabled");
                $j("#repeatInterval").attr("disabled", "disabled");
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
                <label for="dateScheduled">Schedule Date:</label>
                <input type="text" name="dateScheduled" id="dateScheduled" onclick="javascript:NewCssCal ('dateScheduled','ddMMyyyy','dropdown',true,'24')"/>
              <em>or</em>
                <input type="checkbox" name="immediate" id="immediate" value="true"/> Queue Immediately
              <br/><br/>
            <input type="checkbox" name="repeatSchedule" id="repeatSchedule" value="true"/> Check this for Repeat Schedule
            <br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label for="repeatInterval">Repeat Interval:</label>
            <input type="text" name="repeatInterval" id="repeatInterval" disabled="disabled"/>

            <select name="repeatIntervalUnits" id="repeatIntervalUnits" disabled="disabled">
                <option value="seconds">Seconds </option>
                <option value="minutes">Minutes </option>
                <option value="hours">Hours </option>
                <option value="days">Days </option>
            </select>


        </fieldset>
        <fieldset class="visualPadding">
            <legend>Location</legend>
            <select name="facility" id="facility"  size="10">
               <c:forEach var="facility" items="${facilities}">
                    <option value="${facility.facilityId}">${facility.code} - ${facility.name} </option>
               </c:forEach>
            </select>
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
