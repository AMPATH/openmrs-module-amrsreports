<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/amrsreports/viewReport.form" />

<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jquery.tools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/js/ZeroClipboard.js" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/js/jspdf.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/amrsreports/TableTools/css/TableTools_JUI.css" />

<%@ include file="localHeader.jsp"%>

<script type="text/javascript">
	$j(document).ready(function(){

		var ti = $j('#tablehistory').dataTable({
			"bJQueryUI":false,
			"sPaginationType": "full_numbers",
			"sDom": 'T<"clear">lfrtip',
			"oTableTools": {
				"sRowSelect": "single",
				"aButtons": [
					"print"
				]
			}
		});

        var columns = $j('#tablehistory thead tr th').map(function() {

            return $j(this).text();
        });
        columns.splice(0,1);

		$j('#tablehistory').delegate('tbody td #img','click', function() {
			var trow=this.parentNode.parentNode;
			var aData2 = ti.fnGetData(trow);

            $j("#dlgData").empty();
            generate_table(aData2,"dlgData",columns);

           $j("#dlgData").dialog("open");

			return false;
		});

		$j("#dlgData" ).dialog({
			autoOpen:false,
			modal: true,
			show: 'slide',
			height: 'auto',
			hide: 'slide',
			width:600,
			cache: false,
			position: 'middle',
			buttons: {
				"Close": function () { $j(this).dialog("close"); }
			}
		});


		$j('#xlsdownload').click(function() {
			window.open("downloadxls.htm?reportId=${report.id}", 'Download Excel File');
			return false;
		});
	});

    function generate_table(data,bodyDiv,columns) {

        var body = document.getElementById(bodyDiv);
        var tbl     = document.createElement("table");

        tbl.setAttribute('cellspacing','2');
        tbl.setAttribute('border','0');
        tbl.setAttribute('width','100%');
        tbl.setAttribute('class','tblformat');

        tbl.setAttribute('id','tblSummary');


        var tblBody = document.createElement("tbody");

        for(var i=0;i<columns.length;i++){
            tblBody.appendChild(buildRow(columns[i],data[i+1]));

        }

        tbl.appendChild(tblBody);

        body.appendChild(tbl);

    }

    function buildRow(label,tdvalue){

        var row = document.createElement("tr");
        var cell = document.createElement("th");
        cell.setAttribute('align','right');
        var cell2 = document.createElement("td");
        var celllabel = document.createTextNode(label+": ");
        var cellval = document.createTextNode(tdvalue);
        cell.appendChild(celllabel);
        cell2.appendChild(cellval);
        row.appendChild(cell);
        row.appendChild(cell2);
        return row;
    }

	function clearDataTable(){

		dwr.util.removeAllRows("tbodydata");
		var hidepic= document.getElementById("maindetails");
		var titleheader=document.getElementById("titleheader");
		hidepic.style.display='none';
		titleheader.style.display='none';
	}


</script>

<h2>View Report</h2>

<b class="boxHeader">Report Details</b>
<div class="box" style=" width:99%; height:auto;  overflow-x: auto;">

<c:if test="${not empty records}">
    <div id="printbuttons" align="right">
        <input type="button" id="xlsdownload" value="Download Excel Format">
    </div>

    <table id="tablehistory">
        <thead>
            <tr>
                <th>View</th>
                <c:forEach var="col" items="${columnHeaders}">
                    <th>${col}</th>
                </c:forEach>
            </tr>
        </thead>
        <tbody id="tbodydata">
            <c:forEach var="record" items="${records}">
                <tr>
                    <td><img src="${pageContext.request.contextPath}/moduleResources/amrsreports/images/format-indent-more.png" id="img" /></td>
                    <c:forEach var="rec" items="${record}">
                        <td>${rec}</td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<div id="dlgData" title="Patient Information"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
