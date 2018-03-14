/* -- [ report filters ] -- */
$('#step-filters span.text-darken-4').hide();
$('#step-filters span.pink-text').hide();
$('#step-filters span.red-text').click(function () {
    $('.subview-right ul li').not('.fail').hide();
});
$('#step-filters span.green-text').click(function () {
    $('.subview-right ul li').not('.pass').hide();
});
$('#step-filters span.orange-text').click(function () {
    $('.subview-right ul li').not('.warning').hide();
});
$("#step-filters span[status='clear']").click(function () {
    $('.subview-right ul li').css('display', 'list-item');
});

/* -- [ changing the test with 'warning' statuses to have 'pass' statuses ] -- */
$("#test-collection>li.warning>div>span.warning").text('pass').removeClass('warning').addClass('pass');
$("#test-collection>li.warning").removeClass('warning').addClass('pass').attr("status", "pass");


/* -- [ hide the existing doughnut chart ] -- */
$('.chart-box ul.doughnut-legend').hide();


/* -- [ options defined by Extent reports for charts ] -- */
var options = {
    segmentShowStroke: false,
    percentageInnerCutout: 55,
    animationSteps: 1,
    legendTemplate: '<ul class=\'<%=name.toLowerCase()%>-legend right\'><% for (var i=0; i<segments.length; i++) {%><li><%if(segments[i].label && segments[i].value){%><span style=\'background-color:<%=segments[i].fillColor%>\'></span><%=segments[i].label%><%}%></li><%}%></ul>'
};


/* -- [ calculate count of passes fails, skips and warnings for PARENT tests ] -- */
var passesCnt = $('#test-collection li.test.displayed.has-leaf.pass').length;
var failsCnt = $('#test-collection li.test.displayed.has-leaf.fail').length;
var warningCnt = $('#test-collection li.test.displayed.has-leaf.warning').length;
var skipsCnt = $('#test-collection li.test.displayed.has-leaf.skip').length;


/* -- [ updating the displayed value of counts of tests ] -- */
$('#charts-row div.card-panel.nm-v:first div.block.text-small:first span.strong:first').html(passesCnt);
$('#charts-row div.card-panel.nm-v:first div.block.text-small:eq(1) span.strong:first').html(failsCnt);
$('#charts-row div.card-panel.nm-v:first div.block.text-small:eq(1) span.strong:first + span').html(skipsCnt);

/* -- [ data array for parent doughnut ] -- */
var data = [
    {value: passesCnt, color: '#00af00', highlight: '#32bf32', label: 'Pass'},
    {value: failsCnt, color: '#F7464A', highlight: '#FF5A5E', label: 'Fail'},
    {value: statusGroup.fatalParent, color: '#8b0000', highlight: '#a23232', label: 'Fatal'},
    {value: statusGroup.errorParent, color: '#ff6347', highlight: '#ff826b', label: 'Error'},
    {value: warningCnt, color: '#FDB45C', highlight: '#FFC870', label: 'Warning'},
    {value: skipsCnt, color: '#ff8c00', highlight: '#b0ff00', label: 'Skip'}
];

/* -- [ updating the chart ] -- */
var ctx = $("#parent-analysis").get(0).getContext("2d");
var myNewChart = new Chart(ctx).Doughnut(data, options);
drawLegend(myNewChart, 'parent-analysis');


/* -- [ calculate count of passes fails and skips for CHILD tests ] -- */
var passChild = $('div.test-content ul.collapsible.node-list[data-collapsible=accordion] li.node.level-1.leaf.pass[status=pass]').length;
var failChild = $('div.test-content ul.collapsible.node-list[data-collapsible=accordion] li.node.level-1.leaf.fail').length;
var skipChild = $('div.test-content ul.collapsible.node-list[data-collapsible=accordion] li.node.level-1.leaf.skip').length;
var warningChild = $('div.test-content ul.collapsible.node-list[data-collapsible=accordion] li.node.level-1.leaf.warning').length;

/* -- [ correction in child pass count ] -- */
var sauceLabsStepsCount = $("li.test div.node-name:contains('SauceLabs Information')").length;
passChild = passChild - sauceLabsStepsCount;

/* -- [ updating the displayed value of counts of tests ] -- */
$('#charts-row div.card-panel.nm-v:eq(1) div.block.text-small:first span.strong:first').html(passChild);
$('#charts-row div.card-panel.nm-v:eq(1) div.block.text-small:eq(1) span.strong:first').html(failChild);
$('#charts-row div.card-panel.nm-v:eq(1) div.block.text-small:eq(1) span.strong:first + span').html(skipChild + warningChild);

/* -- [ data array for child doughnut ] -- */
var dataChild = [
    {value: passChild, color: '#00af00', highlight: '#32bf32', label: 'Pass'},
    {value: failChild, color: '#F7464A', highlight: '#FF5A5E', label: 'Fail'},
    {value: skipChild, color: '#1e90ff', highlight: '#4aa6ff', label: 'Skip'},
    {value: warningChild, color: '#FDB45C', highlight: '#FFC870', label: 'Warning'},
];

/* -- [ updating the chart ] -- */
var childCtx = $("#child-analysis").get(0).getContext("2d");
var stepChart = new Chart(childCtx).Doughnut(dataChild, options);
drawLegend(stepChart, 'child-analysis');

/* -- [ update ] -- */
var testsCnt = $('#test-collection li.test.displayed').length;
$('#charts-row + div.row:first div.panel-lead:first').html(testsCnt);


function addExpandCollapseAllButtons(color){
		$(".subview-right .test-desc").after("<div style='padding: 3px; text-align: center; border-radius: 4px; border: 1px solid " + color + "; cursor: pointer; position: relative; top: -25px;' class='right' id='expand-all'>EXPAND ALL</div>");
		$(".subview-right .test-desc").after("<div style='padding: 3px; text-align: center; border-radius: 4px; border: 1px solid " + color + "; cursor: pointer; position: relative; top: -25px; display: none;' class='right' id='collapse-all'>COLLAPSE ALL</div>");

		$("#expand-all").click(function(){
			$("#expand-all").hide();
			$("#collapse-all").show();
			$('.subview-right ul li').addClass('active');
			$('.subview-right ul li .collapsible-header').addClass('active');
		    $('.subview-right ul li .collapsible-body').show();
		});

		$("#collapse-all").click(function(){
			$("#collapse-all").hide();
			$("#expand-all").show();
			$('.subview-right ul li').removeClass('active');
			$('.subview-right ul li .collapsible-header').removeClass('active');
			$('.subview-right ul li .collapsible-body').hide();
		});
	}
function fixControlButtonsPosition(top, right){
		var filtersCurrentPosition = $("#step-filters").position();
		$("#step-filters").css({
			"position":"fixed",
			"left": filtersCurrentPosition.left,
			"top":filtersCurrentPosition.top
		});
		$("#expand-all, #collapse-all").css({
			"position":"fixed",
			"right": right,
			"top":top
		});
	}
$(document).ready(function(){
		/*default to 'white' since default theme is dark*/
		var color = 'white';
		addExpandCollapseAllButtons(color);
		var expandCollapseAllCurrentTopPosition = $("#expand-all").position().top;
		var expandCollapseAllCurrentRightPosition = $(window).width() - $("#expand-all").position().left - $("#expand-all").width();
		$("#test-collection>li").click(function(){
			addExpandCollapseAllButtons(color);
			fixControlButtonsPosition(expandCollapseAllCurrentTopPosition, expandCollapseAllCurrentRightPosition);
		});
		fixControlButtonsPosition(expandCollapseAllCurrentTopPosition, expandCollapseAllCurrentRightPosition);
		$("#theme-selector").click(function(){
			/*change the color*/
			(color == 'white') ? color = 'black' : color = 'white';
			$("#collapse-all").css("border", "1px solid " + color);
			$("#expand-all").css("border", "1px solid " + color);
		});
});