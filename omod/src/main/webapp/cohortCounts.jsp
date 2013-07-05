<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Locations" otherwise="/login.htm" redirect="/module/amrsreports/cohortCounts.list"/>

<%@ include file="localHeader.jsp" %>

<script type="text/javascript">

    var reportDate;

    $j(document).ready(function () {

        $j("#update").click(function(event){
            event.preventDefault();
            $j("[name=location]:checked").each(function(){
                var facilityId = $j(this).val();
                getFacilityCount(facilityId, reportDate.getDate(), function(){
                    $j("[name=location][location=" + facilityId + "]").removeAttr("checked");
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

        reportDate = new DatePicker("<openmrs:datePattern/>", "reportDate", { defaultDate: new Date() });
        reportDate.setDate(new Date());
    });

    function getFacilityCount(facilityId, reportDate, callback) {
        $j(".size[location=" + facilityId + "]").html("Calculating ...");
        DWRAmrsReportService.getCohortCountForFacility(facilityId, reportDate, function(size){
            $j(".size[location=" + facilityId + "]").html(size);
            callback();
        });
    }
</script>

<h2>View Cohort Counts</h2>

<b class="boxHeader">Facility Cohorts</b>

<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form>
        <div id="actions">
            <label>Report Date (as of):</label> <br/>
            <input type="text" name="reportDate" id="reportDate"/>
            <button id="update">Update</button>
        </div>
        <table>
            <thead>
            <tr>
                <th><a id="selectAll">All</a> | <a id="selectNone">None</a></th>
                <th>Facility</th>
                <th>Size</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${facilities}" var="facility">
                <tr>
                    <td align="center"><input name="location" type="checkbox" location="${facility.facilityId}" value="${facility.facilityId}"/></td>
                    <td>${facility.name}</td>
                    <td align="right"><span class="size" location="${facility.facilityId}">--</span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
