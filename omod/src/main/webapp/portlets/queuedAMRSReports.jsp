<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/diQuery-collapsiblePanel.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/queuedReportsPortlet.css"/>

<div class="queuedReportsSection">

    <c:if test="${not empty model.title}">
        <div class="queuedReportsSectionTitle">
            ${model.title}
        </div>
    </c:if>

    <div class="queuedReportsSectionContent">

        <c:if test="${empty model.queuedReportsMap}">
            <div class="noReports">
                No reports to display.
            </div>
        </c:if>

        <c:forEach var="f" items="${model.queuedReportsMap}">

            <div class="queuedReportsSubSection">

                <div class="queuedReportsSubSectionTitle">
                    <span class="facilityName">${f.key}</span>
                    <c:if test="${fn:length(f.value) gt 2}">
                        <span class="moreLink">
                            <button class="show_hide" href="#" rel="#${model.status}${f.key.code}">View ${fn:length(f.value) - 2} More</button>
                        </span>
                    </c:if>
                </div>

                <c:set var="extra" value="${false}"/>

                <div class="queuedReportsSubSectionContent">

                    <c:forEach var="r" items="${f.value}" varStatus="status">

                        <c:if test="${status.index == 2 }">
                            <c:set var="extra" value="${true}"/>
                            <div id="${model.status}${f.key.code}" class="extraContent">
                        </c:if>

                        <div class="${status.index % 2 == 0 ? "oddRow" : "evenRow"}">
                            <span class="actions">
                                <c:choose>
                                    <c:when test="${model.status == 'COMPLETE'}">
                                        <a href="viewReport.form?reportId=${r.id}">View</a>
                                        <a href="downloadxls.htm?reportId=${r.id}">Download</a>
                                    </c:when>
                                    <c:when test="${model.status == 'NEW'}">
                                        <a href="queuedReport.form?queuedReportId=${r.id}">Edit</a>

                                    </c:when>
                                </c:choose>
                            </span>

                            <span class="reportName">${r.reportName}</span>

                            <span class="evaluationDate">as of <openmrs:formatDate date="${r.evaluationDate}"
                                                                                   type="textbox"/></span>
                            <c:choose>
                                <c:when test="${model.status == 'NEW'}">

                                        <span class="scheduledDate">run on <openmrs:formatDate date="${r.dateScheduled}"
                                                                                               format="${model.datetimeFormat}"/></span><span class="interval" seconds="${r.repeatInterval}"> &nbsp;&nbsp;tt</span>
                                </c:when>
                                <c:otherwise>
                                     <span class="scheduledDate">ran on <openmrs:formatDate date="${r.dateScheduled}"
                                                                                            format="${model.datetimeFormat}"/></span>
                                </c:otherwise>
                            </c:choose>

                        </div>
                    </c:forEach>

                    <c:if test="${extra == true}">
                </div>
                </c:if>
            </div>
    </div>
    </c:forEach>
</div>
</div>