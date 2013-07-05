<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Locations" otherwise="/login.htm" redirect="/module/amrsreports/facility.list"/>

<script>
    $j(document).ready(function(){
        toggleRowVisibilityForClass('facilityTable', 'voided');
    });
</script>

<%@ include file="localHeader.jsp" %>

<h2>MOH Facilities</h2>

<a href="facility.form">Add a new facility</a>

<br /> <br />

<div class="boxHeader">
	<span style="float: right">
		<a href="#" id="showRetired" onClick="return toggleRowVisibilityForClass('facilityTable', 'voided');"><spring:message code="general.toggle.retired"/></a>
	</span>
    <b>All MOH Facilities</b>
</div>
<div class="box">
    <table cellpadding="2" cellspacing="0" id="facilityTable" width="98%">
        <tr>
            <th> <spring:message code="general.name" /> </th>
            <th> Facility Code </th>
            <th> <spring:message code="general.description" /> </th>
            <th> Locations </th>

        </tr>
        <c:forEach var="facility" items="${facilities}" varStatus="status">
            <tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"} ${facility.retired ? "voided" : ""}'>
                <td valign="top" style="white-space: nowrap"><a href="facility.form?facilityId=${facility.facilityId}">${facility.name}</a></td>
                <td valign="top">${facility.code}</td>
                <td valign="top">${facility.description}</td>
                <td valign="top">${facility.locations}</td>
            </tr>
        </c:forEach>
    </table>
</div>

<br />

<div class="boxHeader">
    <b>Unallocated Locations</b>
</div>
<div class="box">
    <p>
        Click a location below to use it to create a new facility.
    </p>
    <table cellpadding="2" cellspacing="0" id="locationTable" width="98%">
        <tr>
            <th> <spring:message code="general.name" /> </th>
            <th> <spring:message code="general.description" /> </th>
        </tr>
        <c:forEach var="location" items="${unallocatedLocations}" varStatus="status">
            <tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}'>
                <td valign="top" style="white-space: nowrap"><a href="facility.form?locationId=${location.locationId}">${location.name}</a></td>
                <td valign="top">${location.description}</td>
            </tr>
        </c:forEach>
    </table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
