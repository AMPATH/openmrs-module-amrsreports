<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.form"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery-ui-timepicker-addon.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/openmrs-1.9.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/jquery-ui-timepicker-addon.css" />

<%@ include file="localHeader.jsp" %>

<style>
    fieldset.visualPadding { padding: 1em; }
    .right { text-align: right; }
    input.hasDatepicker { width: 14em; }
</style>

<script type="text/javascript">

    var reportDate;
    var scheduleDate;

    $j(document).ready(function () {

        reportDate = new DatePicker("<openmrs:datePattern/>", "reportDate", {
            defaultDate: new Date()
        });
        reportDate.setDate(new Date());

        scheduleDate = new DateTimePicker("<openmrs:datePattern/>", "h:mm tt", "scheduleDate", {
            hourGrid: 6,
            minuteGrid: 10,
            stepMinute: 5
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

<h2>Add Scheduled Report</h2>

<b class="boxHeader">Scheduled Report Details</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST">
        <fieldset class="visualPadding">
            <legend>Dates</legend>
            <table cellspacing="0" cellpadding="2">
                <tr>
                    <td class="right">
                        <label for="reportDate">Report date (as of):</label>
                    </td>
                    <td>
                        <input type="text" name="reportDate" id="reportDate"/>
                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="scheduleDate">Schedule date (run on):</label>
                    </td>
                    <td>
                        <input type="text" id="scheduleDate" name="scheduleDate" value="${now}"/>
                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="repeatSchedule">Make this a repeating schedule:</label>
                    </td>
                    <td>
                        <input type="checkbox" name="repeatSchedule" id="repeatSchedule" value="true"/>
                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="repeatInterval">Repeat Interval:</label>
                    </td>
                    <td>
                        <input type="text" name="repeatInterval" id="repeatInterval" disabled="disabled"/>
                        <select name="repeatIntervalUnits" id="repeatIntervalUnits" disabled="disabled">
                            <option value="minutes">Minutes</option>
                            <option value="hours">Hours</option>
                            <option value="days" selected="selected">Days</option>
                        </select>
                    </td>
                </tr>
            </table>
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
            <c:forEach var="report" items="${reportProviders}">
                <div class="reportProvider<c:if test="${not report.visible}"> hidden</c:if>">
                    <input type="radio" name="reportName" value="${report.name}"/> ${report.name}
                </div>
            </c:forEach>
        </fieldset>
        <input id="submitButton" class="visualPadding newline" type="submit" value="Queue for processing"/>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
