<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
    </li>
    <openmrs:hasPrivilege privilege="Run Reports">
        <li <c:if test='<%= request.getRequestURI().contains("mohRender") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/mohRender.form">
                Run AMRS Reports
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Reports">
        <li <c:if test='<%= request.getRequestURI().contains("mohHistory") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/mohHistory.form">
                View AMRS Reports
            </a>
        </li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="Run Reports">
        <li <c:if test='<%= request.getRequestURI().contains("cohortCounts") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/amrsreports/cohortCounts.list">
                View Cohort Counts
            </a>
        </li>
    </openmrs:hasPrivilege>
    <li <c:if test='<%= request.getRequestURI().contains("locationPrivileges") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/amrsreports/locationPrivileges.form">
            Location Privileges
        </a>
    </li>
</ul>