<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>


<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.form"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery-ui-timepicker-addon.min.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/openmrs-1.9.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/jquery-ui-timepicker-addon.css" />

<%@ include file="localHeader.jsp" %>


<c:if test="${not empty queuedReports.queuedReportId}">

    <h2>Edit Scheduled Report</h2>
</c:if>

<c:if test="${empty queuedReports.queuedReportId}">
    <h2>Add Scheduled Report</h2>
</c:if>

<c:set var="seconds" value="${queuedReports.repeatInterval}"/>
<c:set var="dateScheduled" value="${fn:substringBefore(queuedReports.dateScheduled,'.') }"/>

<c:choose>
    <c:when test="${seconds >= 86400}">
        <c:set var="interval" value="${seconds/(24*60*60)}"/>
        <c:set var="intervalUnit" value="days"/>
    </c:when>
    <c:when test="${seconds < 86400 and seconds >= 3600}">
        <c:set var="interval" value="${seconds/(60*60)}"/>
        <c:set var="intervalUnit" value="hours"/>
    </c:when>
    <c:otherwise>
        <c:set var="interval" value="${seconds/60}"/>
        <c:set var="intervalUnit" value="minutes"/>
    </c:otherwise>
</c:choose>

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
                        <spring:bind path="queuedReports.evaluationDate">
                           <input type="text" name="reportDate" id="reportDate"  value="${status.value}"/>
                        </spring:bind>
                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="scheduleDate">Schedule date (run on):</label>
                    </td>
                    <td>

                            <c:if test="${not empty queuedReports.queuedReportId}">
                                <spring:bind path="queuedReports.dateScheduled">
                                    <input type="text" id="scheduleDate" name="scheduleDate" value="${dateScheduled}" />
                                </spring:bind>
                            </c:if>

                            <c:if test="${empty queuedReports.queuedReportId}">
                                <input type="text" id="scheduleDate" name="scheduleDate" value="${now}"/>
                            </c:if>


                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="repeatSchedule">Make this a repeating schedule:</label>
                    </td>
                    <td>
                        <c:if test="${not empty queuedReports.queuedReportId}">
                            <input type="checkbox" name="repeatSchedule" id="repeatSchedule" value="true" checked="checked" />
                        </c:if>

                        <c:if test="${empty queuedReports.queuedReportId}">
                            <input type="checkbox" name="repeatSchedule" id="repeatSchedule" value="true" />
                        </c:if>

                    </td>
                </tr>
                <tr>
                    <td class="right">
                        <label for="repeatInterval">Repeat Interval:</label>
                    </td>
                    <td>
                        <spring:bind path="queuedReports.repeatInterval">
                            <c:if test="${not empty queuedReports.queuedReportId }">
                                <input type="text" name="repeatInterval" id="repeatInterval" value="${interval}" />
                            </c:if>

                            <c:if test="${empty queuedReports.queuedReportId}">
                                <input type="text" name="repeatInterval" id="repeatInterval"  disabled="disabled"/>
                            </c:if>

                        </spring:bind>

                        <c:if test="${not empty queuedReports.queuedReportId}">
                            <select name="repeatIntervalUnits" id="repeatIntervalUnits" >
                        </c:if>

                        <c:if test="${empty queuedReports.queuedReportId}">
                            <select name="repeatIntervalUnits" id="repeatIntervalUnits" disabled="disabled">
                        </c:if>

                        <c:if test="${not empty queuedReports.queuedReportId}">
                            <option value="minutes" <c:if test="${intervalUnit=='minutes'}">selected</c:if> >Minutes</option>
                            <option value="hours" <c:if test="${intervalUnit=='hours'}">selected</c:if> >Hours</option>
                            <option value="days" <c:if test="${intervalUnit=='days'}">selected</c:if> >Days</option>
                        </c:if>

                        <c:if test="${empty queuedReports.queuedReportId}">
                            <option value="minutes">Minutes</option>
                            <option value="hours">Hours</option>
                            <option value="days" selected="selected">Days</option>
                        </c:if>



                        </select>
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset class="visualPadding">
            <legend>Location</legend>
         <spring:bind path="queuedReports.facility.facilityId">
            <select name="facility" id="facility"  size="10">
               <c:forEach var="facility" items="${facilities}">
                    <option <c:if test="${status.value==facility.facilityId}">selected</c:if> value="${facility.facilityId}">${facility.code} - ${facility.name} </option>
               </c:forEach>
            </select>
          </spring:bind>
        </fieldset>
        <fieldset class="visualPadding">
            <legend>Reports</legend>
            <spring:bind path="queuedReports.reportName">
            <c:forEach var="report" items="${reportProviders}">
                <div class="reportProvider<c:if test="${not report.visible}"> hidden</c:if>">
                    <input type="radio" name="reportName" <c:if test="${status.value==report.name}">checked</c:if> value="${report.name}"/> ${report.name}
                </div>
            </c:forEach>
           </spring:bind>
        </fieldset>
        <input id="submitButton" class="visualPadding newline" type="submit" value="Queue for processing"/>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
