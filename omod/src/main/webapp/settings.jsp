<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Global Properties" otherwise="/login.htm" redirect="/module/amrsreports/cohortCounts.list"/>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAdministrationService.js" />

<style>
    span.saved { font-style: italic; color: green; display: none; }
</style>

<%@ include file="localHeader.jsp" %>

<h2>AMRS Reports Settings</h2>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">

    <table cellpadding="4" cellspacing="0">
        <tr>
            <td>CCC Number Identifier Type</td>
            <td id="cccIdentifierType">
                <form>
                    <input type="hidden" name="property" value="amrsreports.cccIdentifierType"/>
                    <select name="value" onchange="enableSaveFor('cccIdentifierType')">
                        <option value=""></option>
                        <c:forEach items="${identifierTypes}" var="identifierType">
                            <option value="${identifierType.id}"
                            <c:if test="${identifierType.id == cccIdentifierType}">selected</c:if>
                            >${identifierType.name}</option>
                        </c:forEach>
                    </select>
                    <input type="submit" name="save" onclick="return saveGPFor('cccIdentifierType')" value="save"/>
                    <span class="saved">saved</span>
                </form>
            </td>
        </tr>
    </table>

</div>

<script>

    function enableSaveFor(wrapper) {
        $j("#" + wrapper + " input[name=save]").fadeIn();
    }

    function saveGPFor(wrapper) {
        var property = $j("#" + wrapper + " input[name=property]").val();
        var value = $j("#" + wrapper + " select[name=value]").val();

        DWRAdministrationService.setGlobalProperty(property, value, function(){
            $j("#" + wrapper + " input[name=save]").fadeOut("fast", function(){
                $j("#" + wrapper + " span.saved").fadeIn("fast", function(){
                    $j("#" + wrapper + " span.saved").fadeOut(2000);
                });
            });
        });

        return false;
    }

    $j(document).ready(function(){
        $j("input[name=save]").hide();
    });

</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>
