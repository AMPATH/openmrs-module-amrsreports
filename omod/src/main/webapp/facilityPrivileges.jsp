<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Locations,View Users" otherwise="/login.htm" redirect="/module/amrsreports/facilityPrivileges.form"/>

<%@ include file="localHeader.jsp" %>

<script type="text/javascript">

    $j(document).ready(function () {
        $j("#selectAll").click(function (event) {
            event.preventDefault();
            $j("input[name=userFacilityIds]").attr("checked", "checked");
        });

        $j("#selectNone").click(function (event) {
            event.preventDefault();
            $j("input[name=userFacilityIds]").removeAttr("checked");
        });

        $j("#btnAssign").click(function() {
            $j("input[name=action]").val("assign");
        });

        $j("#btnDelete").click(function() {
            $j("input[name=action]").val("delete");
        });
    });

</script>

<style>
    tr.header td, tr.header th { padding-top: 1em; font-weight: bold; border-bottom: 1px solid black; }
</style>

<h2>Manage User / Facility Privileges</h2>

<form method="POST">

    <input type="hidden" name="action" value=""/>

    <b class="boxHeader">Add User / Facility Privilege</b>

    <div class="box" style=" width:99%; height:auto; overflow-x: auto;">
        <table>
            <tr>
                <td><b>User:</b></td>
                <td>
                    <input type="hidden" name="userId" id="userId"/>
                    <openmrs_tag:userField formFieldName="userId"/>
                </td>
            </tr>
            <tr>
                <td><b>Facility:</b></td>
                <td>
                    <select name="facilityId" id="facilityId">
                        <option value="" selected="selected">Select Facility</option>
                        <c:forEach var="facility" items="${facilities}">
                            <option value="${facility.facilityId}">${facility.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
                    <input type="submit" id="btnAssign" name="btnAssign" value="Assign Privilege">
                </td>
            </tr>
        </table>
    </div>

    <br/>

    <b class="boxHeader">Assigned Privileges</b>

    <div class="box" style=" width:99%; height:auto; overflow-x: auto;">
        <input type="submit" id="btnDelete" name="btnDelete" value="Delete Selected">
        <br/>
        <table cellpadding="2" cellspacing="0" id="userFacilityTable">
            <thead>
            <tr class="header">
                <th><a href="#" id="selectAll">All</a> | <a href="#" id="selectNone">None</a></th>
                <th>User</th>
                <th>Facility</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="uf" items="${userFacilities}" varStatus="status">
                <tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}'>
                    <td align="center"><input type="checkbox" name="userFacilityIds" value="${uf.userFacilityId}"></td>
                    <td>${uf.user.personName}</td>
                    <td>${uf.facility.name}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty userFacilities}">
                <tr class="evenRow">
                    <td colspan="3" align="center" style="font-style: italic">No facilities have been assigned yet.</td>
                </tr>
            </c:if>

            <tr class="header">
                <td colspan="3">Super Users (access to all facilities):</td>
            </tr>

            <c:forEach var="u" items="${superusers}" varStatus="status">
                <tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}'>
                    <td>&nbsp;</td>
                    <td>${u.personName}</td>
                    <td>All Facilities</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>