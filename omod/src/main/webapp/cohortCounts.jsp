<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/amrsreport/cohortCounts.list"/>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>

<script type="text/javascript">

    $j(document).ready(function () {

        $j("#download").click(function(event){
            event.preventDefault();

            var evaluationDate = $j("#evaluationDate").val();

            var locations = [];
            $j("[name=location]:checked").each(function(){
                $j(this).removeAttr("checked");
                var locationId = $j(this).val();
                locations.push(locationId);
            });

            var query = $j.param({ 'locations': locations, 'evaluationDate': evaluationDate });
            window.location.href = "<openmrs:contextPath/>/module/amrsreport/downloadCohortCounts.htm?" + query;
        });

        $j("#update").click(function(event){
            event.preventDefault();
            $j("[name=location]:checked").each(function(){
                var locationId = $j(this).val();
                getLocationCount(locationId, function(){
                    $j("[name=location][location=" + locationId + "]").removeAttr("checked");
                });
            });
        });

        $j("#selectAll").click(function(event){
            event.preventDefault();
            $j("[name=location]").each(function(){
                $j(this).attr("checked", "checked");
            });
        });

        $j("#selectNone").click(function(event){
            event.preventDefault();
            $j("[name=location]:checked").each(function(){
                $j(this).removeAttr("checked");
            });
        });

        $j("#evaluationDate").datepicker();
        $j("#evaluationDate").datepicker("setDate", new Date());
        $j("#evaluationDate").datepicker("refresh");
    });

    function getLocationCount(locationId, callback) {
        $j(".size[location=" + locationId + "]").html("Calculating ...");
        var evaluationDate = new Date($j("#evaluationDate").val());
        DWRAmrsReportService.getCohortCountForLocation(locationId, evaluationDate, function(size){
            $j(".size[location=" + locationId + "]").html(size);
            callback();
        });
    }
</script>

<%@ include file="localHeader.jsp" %>

<b class="boxHeader">Location Cohorts</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form>
        <div id="actions">
            <label for="evaluationDate">Evaluation Date:</label>
            <input id="evaluationDate" name="evaluationDate" type="text"/>
            <button id="update">Update Size for Selected Location(s)</button>
            <!--
            <button id="download">Download Cohort List for Selected Location(s)</button>
            -->
        </div>
        <table>
            <thead>
            <tr>
                <th><a id="selectAll">All</a> | <a id="selectNone">None</a></th>
                <th>Location</th>
                <th>Size</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${locations}" var="location">
                <tr>
                    <td align="center"><input name="location" type="checkbox" location="${location.id}" value="${location.id}"/></td>
                    <td>${location.name}</td>
                    <td align="right"><span class="size" location="${location.id}">--</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
