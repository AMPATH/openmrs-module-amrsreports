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
                getFacilityCountByProvider(facilityId, reportDate.getDate(), function(){
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

    function getFacilityCountByProvider(facilityId, reportDate, callback) {
        $j(".size[location=" + facilityId + "]").html("Calculating ...");
        DWRAmrsReportService.getCohortCountForFacilityPerProvider(facilityId, reportDate, function(mapResult){

                for (var key in mapResult) {

                var rptName = key.replace("'", '-');
                $j(".size[location=" + facilityId + "]").filter(".size[reportname=" + rptName +"]").html(mapResult[key]);
            }

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

                <c:forEach var="provider" items="${reportProviders}" varStatus="status">
                    <th>${provider.name}</th>
                </c:forEach>

            </tr>
            </thead>
            <tbody>
            <c:forEach items="${facilities}" var="facility">
                <tr>
                    <td align="center"><input name="location" type="checkbox" location="${facility.facilityId}" value="${facility.facilityId}"/></td>
                    <td>${facility.name}</td>
                    <c:forEach var="provider" items="${reportProviders}" varStatus="status">
                        <td align="left"><div class="size" location="${facility.facilityId}"
                                              reportname="${fn:replace(provider.name,
                                '\'', '-')}">--</div></td>
                    </c:forEach>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
