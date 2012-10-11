<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/ZeroClipboard.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/js/jspdf.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools_JUI.css" />

<%--<openmrs:htmlInclude file="/dwr/interface/DwrUserLocation.js"/>--%>


<script type="text/javascript">
$j(document).ready(function(){

    $j(document).ready(function(){
        var oTable = $j("#avpriv").dataTable();

	$j("#uassign").click(function(){
		//alert("You clicked me Boss!");
		//DwrUserLocation.testDwr(handleResponse);
		var locid=$j("#locname").val();
		var usersid=$j("#seluser").val();
		DwrUserLocation.alertInput(locid,usersid,handleResponse);
		
	}); 
	
	function handleResponse(data){
		alert(data);
	}
	
});
</script>


<b class="boxHeader">UserLocation Privileges</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" name="userlocationpriv" action="mohRender.form">
        <table>
            <tr>
                <td><b>User:</b></td>
                <td>
                    <select name="seluser" id="seluser" multiple="multiple" size="5">
                        <option value="0">Select Users</option>
                        <c:forEach var="suser" items="${userlist}">
                            <option value="${suser.userId}">${suser.username}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td><b>Location:</b></td>
                <td>
                    <select name="locname" id="locname">
                        <option value="0" selected="selected">Select Location</option>
                        <c:forEach var="loc" items="${locationlist}">
                            <option value="${loc.locationId}">${loc.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>

            <tr>
                <td>&nbsp;</td>
                <td>
                    <input type="button" id="uassign" value="Assign Privilege">
                </td>
            </tr>
        </table>
    </form>
</div>
<br>
<b class="boxHeader">Available UserLocation Privileges</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">

    <table id="avpriv" align="left" width="95%">
        <thead>
        <tr>
            <th>&nbsp;</th>
            <th>User</th>
            <th>Location</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <%--<c:forEach var="details" items="${personalAssets}">

            <tr>
                <td>${details.assetno}</td>
                <td>${details.assetname}</td>
                <td>${details.assetlocation}</td>
                <td>${details.assetvalue}</td>
                <td><input type="button" value="Edit" id="editMe" name="${details.assetno}" ></td>
            </tr>

        </c:forEach>--%>
        </tbody>
    </table>

</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>