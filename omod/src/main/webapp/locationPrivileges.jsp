<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/util.js"/>
<%--<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/js/ZeroClipboard.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/js/jspdf.js" />--%>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<%--<openmrs:htmlInclude file="/moduleResources/amrsreport/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/TableTools/css/TableTools_JUI.css" />--%>

<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreport/css/dataTables_jui.css" />

<openmrs:htmlInclude file="/dwr/interface/DWRAmrsReportService.js"/>
<script type="text/javascript">

</script>

<script type="text/javascript">
    $j(document).ready(function(){
      var oTable = $j("#avpriv").dataTable();
        selectedPrivileges = [];

	$j("#uassign").click(function(){

		var locid=$j("#locname").val();
		var usersid=$j("#seluser").val();

        var locidtext=$j('#locname option:selected').text();
        var useridtext =  $j('#seluser option:selected').text();

        DWRAmrsReportService.saveUserLoc(usersid,locid,handleResponse);
        var delbutt='<input type="button" value="Delete" id="deleteMe">';
        var chkbx='<input type="checkbox" >';
        var lstcol="";

        oTable.fnAddData([
            useridtext,
            locidtext ,
            delbutt,
            chkbx,
            lstcol
        ]);
        //this is just for now-- it will be refined later
        window.location.reload();
		
	}); 
	
	function handleResponse(data){
		alert(data);
	}

        $j("#avpriv").delegate('tbody td #deleteMe','click',function(){

            var trow=this.parentNode.parentNode;
            var aData2 = oTable.fnGetData(trow);
            var f0=aData2[0];
            var f1=aData2[4];
            var rowid=$j(this).parents("tr").attr("id");

            DWRAmrsReportService.purgeUserLocation(rowid,purgeResponse);
            window.location.reload();


        });

        function purgeResponse(dataa){
            alert(dataa);
        }

        //-------------------------------------------------
        $j("#avpriv").delegate('tbody td #selMe','click',function(){

            var rowidd=$j(this).parents("tr").attr("id");


            if ($j(this).is(":checked")){
               selectedPrivileges.push(rowidd);

                /*$(this).prop('checked',false);*/
                /*alert("Checked");*/
            }
            else{

                var itempos=selectedPrivileges.indexOf(rowidd);
                selectedPrivileges.splice(itempos,1);
                 /* $(this).prop('checked',true);*/
                /*alert("Not Checked");*/
            }



        });

        $j("#pickAllSel").click(function(){
            DWRAmrsReportService.purgeMultiplePrivileges(selectedPrivileges,testResponse);
            //alert(selectedPrivileges);
        });

        function testResponse(datam){
          alert(datam);
        }

    });
</script>
<div id="dialog-form" title="Create new user" style="display:none;">
    <p class="validateTips">All form fields are required.</p>

    <fieldset>
        <label for="jassetno">Asset No</label>
        <input type="text" name="jassetno" id="jassetno" class="" readonly="readonly" />
        <label for="jassetname">Asset Name</label>
        <input type="text" name="jassetname" id="jassetname" value="" class="" />
        <label for="jlocation">Location</label>
        <input type="text" name="jlocation" id="jlocation" value="" class="" />
        <label for="javalue">Value</label>
        <input type="text" name="javalue" id="javalue" value="" class="" />
    </fieldset>

</div>

<b class="boxHeader">UserLocation Privileges</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">
    <form method="POST" name="userlocationpriv" action="mohRender.form">
        <table>
            <tr>
                <td><b>User:</b></td>
                <td>
                    <select name="seluser" id="seluser" <%--multiple="multiple" size="5"--%>>
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
    <div align="right"><input type="button" id="pickAllSel" value="Process All"></div>

    <table id="avpriv" align="left" width="95%">
        <thead>
        <tr>

            <th>User</th>
            <th>Location</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="details" items="${userlocpriv}">

            <tr id="${details.userLocationId}">

                <td>${details.sysUser}</td>
                <td>${details.userLoc}</td>

                <td><input type="button" value="Delete" id="deleteMe"  ></td>
                <td><input type="checkbox" id="selMe"   ></td>
                <td><input type="hidden" value="${details.userLocationId}"   ></td>
            </tr>

        </c:forEach>
        </tbody>
    </table>

</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>