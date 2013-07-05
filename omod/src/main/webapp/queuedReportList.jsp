<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/queuedReport.list" />

<style>
    .subheading { height: 3em; }
    .subheading th { font-size: 120%; font-weight: normal; text-align: left !important; }
    #reportTable th, #reportTable td { text-align: center; }
</style>

<%@ include file="localHeader.jsp"%>

<a href="queuedReport.form">Add a Scheduled Report</a>

<br />
<br />

<b class="boxHeader">View AMRS Reports</b>
<div class="box">

    <br/>

    <table cellpadding="2" cellspacing="0" id="reportTable" width="98%">

        <c:if test="${not empty queuedReports}">
            <tr class="subheading">
                <th colspan="5">Queued Reports</th>
            </tr>
            <tr>
                <th>Actions</th>
                <th>Report</th>
                <th>Facility</th>
                <th>Evaluation Date</th>
                <th>Scheduled Date</th>
            </tr>
            <c:forEach var="r" items="${queuedReports}" varStatus="status">
                <tr class="queued ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                    <td>
                    </td>
                    <td>${r.reportName}</td>
                    <td>${r.facility}</td>
                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                </tr>
            </c:forEach>
            <tr><td colspan="5">&nbsp;</td></tr>
        </c:if>

        <c:if test="${not empty runningReports}">
            <tr class="subheading">
                <th colspan="5">Running Reports</th>
            </tr>
            <tr>
                <th>Actions</th>
                <th>Report</th>
                <th>Facility</th>
                <th>Evaluation Date</th>
                <th>Scheduled Date</th>
            </tr>
            <c:forEach var="r" items="${runningReports}" varStatus="status">
                <tr class="running ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                    <td>
                    </td>
                    <td>${r.reportName}</td>
                    <td>${r.facility}</td>
                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                </tr>
            </c:forEach>
            <tr><td colspan="5">&nbsp;</td></tr>
        </c:if>

        <c:if test="${not empty completeReports}">
            <tr class="subheading">
                <th colspan="5">Completed Reports</th>
            </tr>
            <tr>
                <th>Actions</th>
                <th>Report</th>
                <th>Facility</th>
                <th>Evaluation Date</th>
                <th>Scheduled Date</th>
            </tr>
            <c:forEach var="r" items="${completeReports}" varStatus="status">
                <tr class="completed ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                    <td>
                        <a href="viewReport.form?reportId=${r.id}">View</a>
                        <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                    </td>
                    <td>${r.reportName}</td>
                    <td>${r.facility}</td>
                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                </tr>
            </c:forEach>
            <tr><td colspan="5">&nbsp;</td></tr>
        </c:if>

        <c:if test="${not empty errorReports}">
            <tr class="subheading">
                <th colspan="5">Reports with errors</th>
            </tr>
            <tr>
                <th>Actions</th>
                <th>Report</th>
                <th>Facility</th>
                <th>Evaluation Date</th>
                <th>Scheduled Date</th>
            </tr>
            <c:forEach var="r" items="${errorReports}" varStatus="status">
                <tr class="errorReport ${status.index % 2 == 0 ? "evenRow" : "oddRow"}">
                    <td>
                    </td>
                    <td>${r.reportName}</td>
                    <td>${r.facility}</td>
                    <td><openmrs:formatDate date="${r.evaluationDate}" type="textbox"/></td>
                    <td><openmrs:formatDate date="${r.dateScheduled}" format="${datetimeFormat}"/></td>
                </tr>
            </c:forEach>
        </c:if>
    </table>

    <br/>

</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
