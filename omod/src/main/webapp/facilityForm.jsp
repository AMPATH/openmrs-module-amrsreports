<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Locations" otherwise="/login.htm" redirect="/module/amrsreports/facility.form"/>

<%@ include file="localHeader.jsp" %>

<c:if test="${not empty facility.facilityId}">
    <h2>Add MOH Facility</h2>
</c:if>

<c:if test="${empty facility.facilityId}">
    <h2>Edit MOH Facility</h2>
</c:if>

<spring:hasBindErrors name="facility">
    <spring:message code="fix.error"/>
    <br />
</spring:hasBindErrors>
<form method="post">
    <fieldset>
        <table>
            <tr>
                <td><spring:message code="general.name"/></td>
                <td>
                    <spring:bind path="facility.name">
                        <input type="text" name="name" value="${status.value}" size="35" />
                        <c:if test="${status.errorMessage != ''}"><c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td valign="top"><spring:message code="general.description"/></td>
                <td valign="top">
                    <spring:bind path="facility.description">
                        <textarea name="description" rows="3" cols="40" onkeypress="return forceMaxLength(this, 1024);" >${status.value}</textarea>
                        <c:if test="${status.errorMessage != ''}"><c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td valign="top">Facility Code</td>
                <td valign="top">
                    <spring:bind path="facility.code">
                        <input type="text" name="code" value="${status.value}" size="10" />
                        <c:if test="${status.errorMessage != ''}"><c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <td valign="top">Locations</td>
                <td valign="top">
                    <openmrs:listPicker name="locations" allItems="${locations}" currentItems="${facility.locations}" inheritedItems="${allocatedLocations}"/>
                </td>
            </tr>
            <c:if test="${!(facility.creator == null)}">
                <tr>
                    <td><spring:message code="general.createdBy" /></td>
                    <td>
                        ${facility.creator.personName} -
                        <openmrs:formatDate date="${facility.dateCreated}" type="long" />
                    </td>
                </tr>
            </c:if>
            <c:if test="${!(facility.changedBy == null)}">
                <tr>
                    <td><spring:message code="general.changedBy" /></td>
                    <td>
                        ${facility.changedBy.personName} -
                        <openmrs:formatDate date="${facility.dateChanged}" type="long" />
                    </td>
                </tr>
            </c:if>
        </table>
        <br />

        <input type="submit" value="<spring:message code="general.save"/>" name="save">

    </fieldset>
</form>

<br/>

<c:if test="${not facility.retired && not empty facility.facilityId}">
    <form method="post">
        <fieldset>
            <h4><spring:message code="general.retire"/></h4>

            <b><spring:message code="general.reason"/></b>
            <input type="text" value="" size="40" name="retireReason" />
            <spring:hasBindErrors name="facility">
                <c:forEach items="${errors.allErrors}" var="error">
                    <c:if test="${error.code == 'retireReason'}"><span class="error"><spring:message code="${error.defaultMessage}" text="${error.defaultMessage}"/></span></c:if>
                </c:forEach>
            </spring:hasBindErrors>
            <br/>
            <input type="submit" value='<spring:message code="general.retire"/>' name="retire"/>
        </fieldset>
    </form>
</c:if>

<c:if test="${facility.retired && not empty facility.facilityId}">
    <form method="post">
        <fieldset>
            <h4><spring:message code="general.unretire"/></h4>
            <br/>
            <input type="submit" value='<spring:message code="general.unretire"/>' name="unretire"/>
        </fieldset>
    </form>
</c:if>

<br/>

<c:if test="${not empty facility.facilityId}">
    <form id="purge" method="post" onsubmit="return confirmPurge()">
        <fieldset>
            <h4><spring:message code="general.purge"/></h4>
            <input type="submit" value='<spring:message code="general.purge"/>' name="purge" />
        </fieldset>
    </form>
</c:if>



<%@ include file="/WEB-INF/template/footer.jsp" %>
