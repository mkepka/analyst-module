/**
 * File containing main functions for Analyst web application
 * @author Michal Kepka
 * @license BSD 3-clause license
 */

"use strict";
var user_id = 'tester';
var user_param = '?user_id=';

var app_name = 'analyst-portal';
var main_url = window.location.origin+'/'+app_name+'/';

/**
 * Function loads data from Web service
 * /rest/query
 */
function fillCollapsibleList(){
    $.getJSON(main_url+'rest/query'+user_param+user_id, function(data) {
        $.each(data, function(index, item) {
            var e_id = '#'+item.query_id;
            // append item in finished state
            if(item.processing_status === 'finished'){
                $('#right').append(composeFinishedItem(item));
            }
            // append item in processing state
            else if(data.processing_status === 'processing'){
                $('#right').append(composeProcessingItem(item));
            }
            addDataToElement(e_id, item);
        });
        
        // adding event to collapsible list
        addCollabsibleEvent('.collapsible');
        
        // add event to preview result data
        $('.query_preview').on("click", function(){
            previewQueryResultData(this);
        });
        // add event to download result data 
        $('.query_download').on("click", function(){
            downloadResult(this);
        });
    });
}

/**
 * Function adds event to the collapsible menu
 * and highlights SQL code
 * @param element_name - name of the element to add event 
 */
function addCollabsibleEvent(element_name){
    $(element_name).on("click", function() {
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.maxHeight){
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px"; /* zde je nejspíš chyba při prvním rozbalení */
        }
    });
    // highlighting SQL
    $('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });
}

/**
 * Function adds meta data about StoredQuery to given element
 * @param element_id - ID of element to store data
 * @param data - StoredQuery meta data
 */
function addDataToElement(element_id, data){
    $(element_id).data('data', data);
}

/**
 * Function adds result data of StoredQuery to given element
 * @param element_id - ID of element to store data
 * @param data - StoredQuery meta data
 */
function addResultDataToElement(element_id, data){
    $(element_id).data('result', data);
}

/**
 * Function creates one element of collapsible menu containing one query
 * @param item - StoredQuery from DB as JSON
 * @returns {String} to be places in HTML element
 */
function composeFinishedItem(selQuery){
    var but_id = 'but-'+selQuery.query_id;
    var button = '<button id='+but_id+' class="collapsible">'+selQuery.query_name+'</button>';
    var tab = '<div id='+selQuery.query_id+' class="content">';
    tab = tab + '<table class="query_meta"><tbody class="query_meta">';
    tab = tab + '<tr class="query_meta_row"><td class="query" colspan="2"><code>'+selQuery.query+'</code></td></tr>';
    tab = tab + '<tr class="query_meta_row"><td class="features" colspan="2">'+FEATURE_COUNT+': '+selQuery.feature_count+'</td></tr>';
    tab = tab + '<tr class="query_meta_row"><td class="query_actions"><button class="query_preview">'+RESULT_PREVIEW+'</button></td>';
    tab = tab + '<td class="query_downlds">';
    if(selQuery.has_geometry === true){
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/kml'+user_param+user_id
            +'" download="Result_'+selQuery.query_id+'.kml"><img class="data_download" src="icons/kml.icon.svg" alt="KML"></a>';
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/geojson'+user_param+user_id+'" target="_blank"><img class="data_download" src="icons/geojson.icon.svg" alt="GeoJSON"></a>';
    }
    else{
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/csv'+user_param+user_id
            +'" download="Result_'+selQuery.query_id+'.csv"><img class="data_download" src="icons/csv.icon.svg" alt="CSV"></a>';
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/json'+user_param+user_id+'" target="_blank"><img class="data_download" src="icons/json.icon.svg" alt="JSON"></a>';
    }
    tab = tab + '</td></tr></tbody></table></div>';
    var cont = button + tab;
    return cont;
}

function composeFinishedItemJS(element, selQuery){
    var but = document.createElement("BUTTON");
    but.id = 'but-'+selQuery.query_id;
    but.className = 'collapsible';
    var but_text = document.createTextNode(selQuery.query_name);
    but.appendChild(but_text);
    element.appendChild(but);
    
    var div = document.createElement("DIV");
    div.id = selQuery.query_id;
    div.className = 'content';

    var table = document.createElement("TABLE");
    table.className = 'query_meta';
    var tbody = document.createElement("TBODY");
    tbody.className = 'query_meta';
    
    var row_query = tbody.insertRow(0);
    row_query.className = 'query_meta_row';
    var cell_query = row_query.insertCell(0);
    cell_query
}

/**
 * Function creates one element of collapsible menu containing one processed query
 * @param selQuery - object with partial meta data about new Query
 */
function composeProcessingItem(selQuery){
    var but_id = 'but-'+selQuery.query_id;
    var button = '<button id='+but_id+' class="processing collapsible">'+selQuery.query_name+'</button>';
    var tab = '<div id='+selQuery.query_id+' class="content">';
    tab = tab + '<table class="query_meta"><tbody class="query_meta">';
    tab = tab + '<tr class="query_meta_row"><td class="query" colspan="2"><code>'+selQuery.query+'</code></td></tr>';
    tab = tab + '<tr class="query_meta_row"><td class="features" colspan="2">'+FEATURE_COUNT+': </td></tr>';
    tab = tab + '<tr class="query_meta_row"><td class="query_actions">';
    tab = tab + '<button class="query_check_status">'+QUERY_STATUS+'</button></td></tr>';
    tab = tab + '</tbody></table></div>';
    var cont = button + tab;
    return cont;
}

/**
 * Function loads query result data to main window
 * @param element - element that called this function
 */
function previewQueryResultData(element){
    var query_data = $(element).parents('.content').data('data');
    var result_data = $(element).parents('.content').data('result');

    // show Query meta data
    showQueryMetaData(query_data);

    if(result_data === undefined){
        // load geometry data
        if(query_data.has_geometry === true){
            loadGeometryData(query_data);
            openPage('Map', document.getElementById('map_button'));
        }
        // load tabular data
        else{
            loadTabularData(query_data);
            openPage('Table', document.getElementById('tab_button'));
        }
    }
    else{
        // show previously downloaded result data
        if(query_data.has_geometry === true){
            clearGeoJsonLayer(); // from map.js
            document.getElementById("dataTable").innerHTML = "";

            addGeoData(result_data);
            buildHtmlTable(extractProperties(result_data), '#dataTable');
            openPage('Map', document.getElementById('map_button'));
        }
        else{
            clearGeoJsonLayer(); // from map.js
            document.getElementById("dataTable").innerHTML = "";

            buildHtmlTable(result_data, '#dataTable');
            openPage('Table', document.getElementById('tab_button'));
        }
    }
} 

/**
 * Function extracts properties from GeoJSON data to build HTML table 
 * @param data - GeoJSON
 * @returns {Array} - Array with properties
 */
function extractProperties(data){
    var len = data.features.length;
    var tab = [];
    for(var i = 0; i < len; i++){
        tab[i] = data.features[i].properties;
    }
    return tab;
}

/**
 * Function fill in table on top div to show meta data of selected Query
 * @param selQuery - object of StoredQuery
 */
function showQueryMetaData(selQuery){
    $('#sql_name').val(selQuery.query_name);
    $('#sql_name').data('data', selQuery);
    $('#sql_txt').val(selQuery.query);
}

/**
 * Function loads data of the query 
 * @param selQuery - object of Query meta data
 */
function loadTabularData(selQuery){ 
    $.get(main_url+"rest/query/"+selQuery.query_id+"/json"+user_param+user_id, function(data, status){
        clearGeoJsonLayer(); // from map.js
        document.getElementById("dataTable").innerHTML = "";

        addResultDataToElement('#'+selQuery.query_id, data)
        buildHtmlTable(data, '#dataTable');
    });
}

/**
 * Builds the HTML Table out of myList
 * @param table - data to fill in table
 * @param selector - selector where to put HTML table
 */
function buildHtmlTable(table, selector) {
    var columns = addAllColumnHeaders(table, selector);
    for (var i = 0; i < table.length; i++) {
        var row$ = $('<tr/>');
    for (var colIndex = 0; colIndex < columns.length; colIndex++) {
      var cellValue = table[i][columns[colIndex]];
      if (cellValue == null) cellValue = "";
      row$.append($('<td/>').html(cellValue));
    }
    $(selector).append(row$);
  }
}

/**
 * Adds a header row to the table and returns the set of columns.
 * Need to do union of keys from all records as some records may not contain
 * all records.
 * @param table - data to fill in table
 * @param selector - selector where to put HTML table 
 */
function addAllColumnHeaders(table, selector) {
    var columnSet = [];
    var headerTr$ = $('<tr/>');
    for (var i = 0; i < table.length; i++) {
        var rowHash = table[i];
        for (var key in rowHash) {
            if ($.inArray(key, columnSet) == -1) {
                columnSet.push(key);
                headerTr$.append($('<th/>').html(key));
            }
        }
    }
    $(selector).append(headerTr$);
    return columnSet;
}

/**
 * Function loads spatial data of the query 
 * @param selQuery - object of Query meta data
 */
function loadGeometryData(selQuery){
    $.get(main_url+"rest/query/"+selQuery.query_id+"/geojson"+user_param+user_id, function(data, status){
        addResultDataToElement('#'+selQuery.query_id, data)
        document.getElementById("dataTable").innerHTML = "";
        addGeoData(data);
        buildHtmlTable(extractProperties(data), '#dataTable');
    });
}

/**
 * Function switching main page tabs
 * @param pageName - name of page to be switched on
 * @param elmnt - instance of element called function
 */
function openPage(pageName, elmnt) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablink");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].style.backgroundColor = "";
    }
    document.getElementById(pageName).style.display = "block";
    elmnt.style.backgroundColor = 'lightgrey';
}

/**
 * Function checks status of processing query
 * @param element - button that was clicked
 */
function checkQueryStatus(element){
    var query_data = $(element).parents('.content').data('data');
    // clean table 
    clearGeoJsonLayer(); // from map.js
    $("#dataTable").empty();
    
    showQueryMetaData(query_data);
    loadQueryStatus(query_data);
}

/**
 * Function check status of given query in the DB
 * @param query - object of Query meta data
 */
function loadQueryStatus(query){
    $.getJSON(main_url+"rest/query/"+query.query_id+"/status"+user_param+user_id, function(data) {
        if(data.processing_status === 'finished'){
            // load query meta data from Server
            loadQueryMetaData(query);
        }
        else{
            window.alert(QUERY_IS_RUNNING);
        }
    });
}

/**
 * Function loads Query meta data and adds result to Collapsible list
 * @param query - object of Query meta data
 */
function loadQueryMetaData(query){
    $.getJSON(main_url+"rest/query/"+query.query_id+user_param+user_id, function(data){
        var elem_id = '#'+data.query_id;
        var elem = $(elem_id);
        if(elem === undefined){
            // add new item to Collapsible list
            if(data.processing_status === 'finished'){
                $('#right').append(composeFinishedItem(data));
             // add event to preview query results
                $(elem_id).find('.query_preview').on("click", function(){
                    previewQueryResultData(this);
                });
            }
            // append item in processing state
            else if(data.processing_status === 'processing'){
                $('#right').append(composeProcessingItem(data));
                // add event to check query status
                $(elem_id).find('.query_check_status').on("click", function(){
                    checkQueryStatus(this);
                });
            }
            // adding event to new collapsible item
            addCollabsibleEvent('#but-'+data.query_id);
            
        }
        else{
            // update existing item in Collapsible list
            if(data.processing_status === 'finished'){
                updCollabsibleElemFinished(data);
            }
            else if(data.processing_status === 'processing'){
                updCollabsibleElemProcessing(data);
            }
        }
        // store Query meta data to the element
        addDataToElement(elem_id, data);
    });
}
/**
 * Function remove item from Collabsible list
 * @param query - object of Query meta data
 * 
 */
function removeCollabsibleElement(query){
    $('#'+query.query_id).remove();
    $('#but-'+query.query_id).remove();
}

/**
 * Function primary checks SQL query to be sent to the Server 
 * @param query - query to be checked as String
 * @returns {Boolean} - true if query is OK
 */
function checkInsertingQuery(query){
    var lowerQuery = query.toLowerCase();
    var n = lowerQuery.search("drop");
    if(n === -1){
        return true;
    }
    else{
        return false;
    }
}

/**
 * Function update item from Collapsible list to processing state
 * @param query - Query meta data object
 */
function updCollabsibleElemProcessing(query){
    var content = $('#'+query.query_id);
    content.find("td.query").html("<code>"+query.query+"</code>");
    content.find("td.features").text(FEATURE_COUNT+": ");
    content.find("td.query_actions").html('<button class="query_check_status">'+QUERY_STATUS+'</button>');
    content.find("td.query_downlds").html('');
    $('#but-'+query.query_id).attr("class", "processing collapsible");
    $('#but-'+query.query_id).text(query.query_name);
    
    // highlighting SQL
    content.find('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });
    
    // add event to check query status
    content.find('.query_check_status').on("click", function(){
        checkQueryStatus(this);
    });
}

/**
 * Function update item from Collapsible list to finished state
 * @param query - Query meta data object
 */
function updCollabsibleElemFinished(selQuery){
    var content = $('#'+selQuery.query_id);
    content.find("td.query").html("<code>"+selQuery.query+"</code>");
    content.find("td.features").text(FEATURE_COUNT+": "+selQuery.feature_count);
    // change query action button
    content.find("td.query_actions").html('<button class="query_preview">'+RESULT_PREVIEW+'</button>');
    // create download links
    var tab = '';
    if(selQuery.has_geometry === true){
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/kml'+user_param+user_id
            +'" download="Result_'+selQuery.query_id+'.kml"><img class="data_download" src="icons/kml.icon.svg" alt="KML"></a>';
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/geojson'+user_param+user_id+'" target="_blank"><img class="data_download" src="icons/geojson.icon.svg" alt="GeoJSON"></a>';
    }
    else{
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/csv'+user_param+user_id
            +'" download="Result_'+selQuery.query_id+'.csv"><img class="data_download" src="icons/csv.icon.svg" alt="CSV"></a>';
        tab = tab + '<a href="rest/query/'+selQuery.query_id+'/json'+user_param+user_id+'" target="_blank"><img class="data_download" src="icons/json.icon.svg" alt="JSON"></a>';
    }
    if(content.find("td.query_downlds").length == 1){
        content.find("td.query_downlds").html(tab);
    }
    else{
        var act_row = content.find("td.query_actions").parents('.query_meta_row');
        act_row.append('<td class="query_downlds">'+tab+'</td>');
    }
    // change item layout and name
    $('#but-'+selQuery.query_id).attr("class", "collapsible active");
    $('#but-'+selQuery.query_id).text(selQuery.query_name);
    
    // highlighting SQL
    content.find('code').each(function(i, block) {
        hljs.highlightBlock(block);
    });
    
    // add event to preview result data
    content.find('.query_preview').on("click", function(){
        previewQueryResultData(this);
    });
}

/**
 * Function sends SQL query to run and store in DB
 * /rest/query?user_id=&uses_time=
 */
function insertQuery(){
    var name = $("#sql_name").val();
    var sql = $("#sql_txt").val();
    if ((!name || name.length === 0) && (!sql || sql.length === 0)){
        window.alert(ENTER_QUERY_NAME);
    }
    else if ((!name || name.length === 0) || (!sql || sql.length === 0)){
        window.alert(ENTER_QUERY_NAME);
    }
    else{
        var isOk = checkInsertingQuery(sql);
        if(!isOk){
            window.alert(QUERY_CONTAINS_WRONG_WORD);
        }
        else{
            $.ajax({
                type: 'POST',
                url: main_url+'rest/query'+user_param+user_id,
                contentType : 'application/json',
                data: JSON.stringify({query_name:name, query:sql}),
                success: function(data, textStatus, jqXHR){
                    var new_query = {query_id:data.query_id, query_name:name, query:sql, status:data.processing_status};
                    var elem_id = '#'+data.query_id;
                    
                    // append new item to Collapsible list
                    $('#right').append(composeProcessingItem(new_query));
                    addDataToElement(elem_id, new_query);
                    // add event to roll up list
                    addCollabsibleEvent('#but-'+data.query_id);
                    
                    // add event to check query status
                    $(elem_id).find('.query_check_status').on("click", function(){
                        checkQueryStatus(this);
                    });
                },
                error: function(jqXHR, textStatus, errorThrown){
                    window.alert(QUERY_NOT_ACCEPTED);
                }
             });
        }
    }
}

/**
 * Function updates selected StoredQuery in DB 
 */
function updateQuery(){
    var selQuery = $('#sql_name').data('data');
    if(selQuery == undefined){
        window.alert(SELECT_QUERY_TO_UPDATE);
    }
    else{
        var name = $('#sql_name').val();
        var sql = $("#sql_txt").val();
        
        if ((!name || name.length === 0) && (!sql || sql.length === 0)){
            window.alert(ENTER_QUERY_NAME);
        }
        else if ((!name || name.length === 0) || (!sql || sql.length === 0)){
            window.alert(ENTER_QUERY_NAME);
        }
        else{
            var upd_sql = {query_id: selQuery.query_id, query_name:name, query:sql};
            $.ajax({
                type: 'PUT',
                url: main_url+'rest/query/'+selQuery.query_id+user_param+user_id,
                contentType : 'application/json',
                data: JSON.stringify(upd_sql),
                success: function(data, textStatus, jqXHR){
                    // remover layer
                    if(selQuery.has_geometry === true){
                      clearGeoJsonLayer(); // from map.js
                    }
                    //remove content from table#dataTable
                    $("#dataTable").empty();
                    
                    var upd_query = {query_id:selQuery.query_id, query_name:name, query:sql, status:"processing"};
                    // change collapsible item to processing state
                    updCollabsibleElemProcessing(upd_query);
                    // add meta data to element
                    var elem_id = '#'+upd_query.query_id;
                    addDataToElement(elem_id, upd_query);
                    // remove old result data
                    $(elem_id).removeData('result');
                },
                error: function(jqXHR, textStatus, errorThrown){
                    window.alert(QUERY_NOT_ACCEPTED);
                }
            });
        }
    }
}
/**
 * Function deletes selected StoredQuery from DB
 */
function deleteQuery(){
    var selQuery = $('#sql_name').data('data');
    if(selQuery == undefined){
        window.alert(QUERY_NOT_SELECTED);
    }
    else{
        if (window.confirm(REALLY_DELETE_QUERY)){
            $.ajax({
                type: 'DELETE',
                url: main_url+'rest/query/'+selQuery.query_id+user_param+user_id,
                contentType : 'application/json',
                success: function(data, textStatus, jqXHR){
                    // remove layer
                    if(selQuery.has_geometry === true){
                      clearGeoJsonLayer(); // from map.js
                    }
                    //remove content from table#dataTable
                    $("#dataTable").empty();
                    //remove content from div#sql
                    $('#sql_name').removeData('data');
                    $("#sql_name").val("");
                    $("#sql_txt").val("");
                    // remove item from Collabsible list
                    removeCollabsibleElement(selQuery);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert('updateWine error: ' + textStatus);
                }
            });
        }
    }
}

/**
 * Main function
 */
$(document).ready(function(){
    $('#sql_name')[0].attributes.placeholder.nodeValue = SQL_NAME_TEXT;
    $('#sql_txt')[0].attributes.placeholder.nodeValue = SQL_QUERY_TEXT;

    $('#execute').text(QUERY_RUN);
    $('#update').text(QUERY_UPDATE);
    $('#delete').text(QUERY_DELETE);

    $('#map_button').text(MAP_TAB);
    $('#tab_button').text(ATTRIBUTE_TAB);

    
    fillCollapsibleList();
    /*
     * Setting map height
     */
    var outHeight;
    if($('.portlet-dockbar').length === 0 && $('#banner').length === 0){
        outHeight = $('#top').height();
    }
    else{
        outHeight = $('.portlet-dockbar').outerHeight() + $('#banner').outerHeight() + $('#top').outerHeight();
    }
    var mainHeight = $(window).innerHeight() - outHeight;
    $('#main_area').height(mainHeight);
    var mapHeight = mainHeight - $('#map_button').outerHeight();
    if (mapHeight < 500) mapHeight = 500;
    $('#map_area').height(mapHeight);
    /*
     * Initialize map window
     */
    openPage('Map', document.getElementById('map_button'));
    initializeMap();
    
    /*
     * Click buttons in top row
     */
    $('#execute').click(function(event){
        insertQuery();
    });

    $('#delete').click(function(){
        deleteQuery();
    });

    $('#update').click(function(event){
        updateQuery();
    });
});