<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<openmrs:htmlInclude file="/moduleResources/amrsreports/css/amrsreports.css" />

<script>
    $j(document).ready(function(){
        $j("#runningTaskAlert").hide();

        DWRAmrsReportService.isReportRunnerScheduledTaskOn(function(response){
            if (response == false) {
                $j("#runningTaskAlert").fadeIn();
            }
        });
    });
</script>

<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
    </li>
    <openmrs:hasPrivilege privilege="View Reports">
        <li <c:if test='<%= request.getRequestURI().contains("queuedReport") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/queuedReport.list">
                Manage AMRS Reports
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Locations">
        <li <c:if test='<%= request.getRequestURI().contains("cccNumbers") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/cccNumbers.list">
                Manage CCC Numbers
            </a>
        </li>
        <li <c:if test='<%= request.getRequestURI().contains("facilityList") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/facility.list">
                View MOH Facilities
            </a>
        </li>
        <openmrs:hasPrivilege privilege="View Users">
            <li <c:if test='<%= request.getRequestURI().contains("facilityPrivileges") %>'>class="active"</c:if>>
                <a href="${pageContext.request.contextPath}/module/amrsreports/facilityPrivileges.form">
                    Manage User/Facility Privileges
                </a>
            </li>
        </openmrs:hasPrivilege>
        <li <c:if test='<%= request.getRequestURI().contains("cohortCounts") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/cohortCounts.list">
                View Cohort Counts
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Global Properties">
        <li <c:if test='<%= request.getRequestURI().contains("settings") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/settings.form">
                Settings
            </a>
        </li>
    </openmrs:hasPrivilege>
</ul>

<div id="runningTaskAlert" class="visualPadding hidden error">
    The scheduled task for running queued reports is not turned on.  Please
    <a href="<openmrs:contextPath/>/admin/scheduler/scheduler.list">activate it</a> or ask an administrator to do the same.
</div>
