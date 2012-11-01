<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/util.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
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

	}); 
	
	function handleResponse(data){
		alert(data);
        if(data=="The privilege already exist"){

        }
        else{
            //TODO reload datatable and not the whole page.
            window.location.reload(true);
        }

	}

        $j("#avpriv").delegate('tbody td #deleteMe','click',function(){

            var trow=this.parentNode.parentNode;
            var aData2 = oTable.fnGetData(trow);
            var f0=aData2[0];
            var f1=aData2[4];
            var rowid=$j(this).parents("tr").attr("id");


            $j( "#dialog-form" ).dialog({
                height: 150,
                width: 'auto',
                modal: true,
                buttons:{
                    "OK":function(){
                        DWRAmrsReportService.purgeUserLocation(rowid,purgeResponse);
                        $j(this).dialog( "close" );



                    },
                    Cancel: function() {
                        $j(this).dialog( "close" );
                    }
                }

            });


        });

        function purgeResponse(dataa){
            alert(dataa);
            //TODO reload datatable and not the whole page.
            window.location.reload(true);
        }

        //-------------------------------------------------
        $j("#avpriv").delegate('tbody td #selMe','click',function(){

            var rowidd=$j(this).parents("tr").attr("id");


            if ($j(this).is(":checked")){
               selectedPrivileges.push(rowidd);

            }
            else{

                var itempos=selectedPrivileges.indexOf(rowidd);
                selectedPrivileges.splice(itempos,1);

            }



        });

        $j("#pickAllSel").click(function(){

            $j( "#dialog-form" ).dialog({
                height: 150,
                width: 'auto',
                modal: true,
                buttons:{
                    "OK":function(){
                     DWRAmrsReportService.purgeMultiplePrivileges(selectedPrivileges,testResponse);
                     $j(this).dialog( "close" );
                    },
                    Cancel: function() {
                        $j(this).dialog( "close" );
                    }
                }

            });

        });

        function testResponse(datam){
          alert(datam);
            //TODO reload datatable and not the whole page.
          window.location.reload(true);
        }

    });
</script>
<div id="dialog-form" title="Delete UserLocation Privileges" style="display:none;">
    <p class="validateTips">Are you sure you want to delete the selected privileges?</p>
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

        </tr>
        </thead>
        <tbody>
        <c:forEach var="details" items="${userlocpriv}">

            <tr id="${details.userLocationId}">

                <td>${details.sysUser}</td>
                <td>${details.userLoc}</td>

                <td><input type="button" value="Delete" id="deleteMe"  ></td>
                <td><input type="checkbox" id="selMe"   ></td>

            </tr>

        </c:forEach>
        </tbody>
    </table>

</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>