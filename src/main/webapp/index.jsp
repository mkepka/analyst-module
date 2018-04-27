<%@ page contentType="text/html; charset=UTF-8" %>
<html>
    <head>
        <meta name="author" content="Michal Kepka">
        <meta name="description" content="PostMap client">
        <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0">
        <title>PostMap client 1.2.2</title>
        
        <!-- import -->
        <script type="text/javascript" src="lib/jquery-3.3.1.js"></script>
        <link rel="stylesheet" href="css/highlight_styles/vs.css">
        <script type="text/javascript" src="lib/highlight.pack.js"></script>
        <script type="text/javascript" src="lib/jquery-ui-1.12.1.js"></script>
        <script type="text/javascript" src="lib/proj4-compressed.js"></script>
        <link rel="stylesheet" href="css/leaflet.css" />
        <script type="text/javascript" src="lib/leaflet.js"></script>
        <script type="text/javascript" src="lib/proj4-compressed.js"></script>
        <script type="text/javascript" src="lib/proj4leaflet.js"></script>
        
        <!-- vlastni skripty -->
        <link rel="stylesheet" href="css/layout.css" />
        <script type="text/javascript" src="js/language.js"></script>
        <script type="text/javascript" src="js/scripts.js"></script>
        <script type="text/javascript" src="js/map.js"></script>
        
    </head>
    <body>
        <div id="main" class="container">
            <div id="left" class="container">
                <div id="top" class="top">
                    <div id="sql">
                        <textarea id="sql_name" placeholder=""></textarea>
                        <textarea id="sql_txt" placeholder=""></textarea>
                    </div>
                    <div id="sql_buttons" class="btn-group">
                        <button class="sql_button" id="execute"></button>
                        <button class="sql_button" id="update"></button>
                        <button class="sql_button" id="delete"></button>
                    </div>
                </div>
                <div class="clear"></div>
                <div id="main_area">
                    <button id="map_button" class="tablink" onclick="openPage('Map', this)"></button>
                    <button id="tab_button" class="tablink" onclick="openPage('Table', this)"></button>
                    <div id="Map" class="tabcontent">
                        <div id="map_area"></div>
                    </div>
                    <div id="Table" class="tabcontent">
                        <table id="dataTable"></table>
                    </div>
                </div>
            </div>
            <div id="right" class="container"></div>
        </div>
    </body>
</html>