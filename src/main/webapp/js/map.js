/**
 * File containing main functions for map window of the Analyst web application
 * @author Michal Kepka
 * @license BSD 3-clause license
 */

'use strict';
var mymap;
var crs_krovak;
var layerGeoJsonGeog;
var layerGeoJsonProj;
var centr_lat = 49.78552637558463;
var centr_lon = 15.646784349776558;

function initializeMap() {
    crs_krovak = new L.Proj.CRS("EPSG:5514","+proj=krovak +lat_0=49.5 +lon_0=24.83333333333333 +alpha=30.28813972222222 +k=0.9999 +x_0=0 +y_0=0 "+
        "+ellps=bessel +towgs84=572.213,85.334,461.94,-4.9732,-1.529,-5.2484,3.5378 +units=m +no_defs",
        {
            resolutions: [1618.8602350592, 809.4301175296, 404.7150587648, 202.3575293824, 101.1787646912, 50.5893823456, 25.2946911728, 12.6473455864, 6.3236727932, 3.1618363966],
            bounds: L.bounds([-930000, -1245000], [-100000, -830000])
        });
    //origin: [-920001, -1240000],
    //bounds: L.bounds([-929903.9375, -1244868.75], [-101047.49714968959, -830440.5298248448])
    //bounds: L.bounds([-920001, -1240000], [-420001, -929999])
    //resolutions: [3251.1999, 1625.5999, 812.7999, 406.3999, 203.1999, 101.5999, 50.7999, 25.3999, 12.6999, 6.3499, 3.1749, 1.5874],
    mymap = L.map('map_area', {
        crs: crs_krovak
    });
    
    // Set base maps
    var basemaps = {
            Cenia_IIIVM: L.tileLayer.wms('https://geoportal.gov.cz/ArcGIS/services/CENIA/cenia_rt_III_vojenske_mapovani/MapServer/WMSServer?', {
                layers: '0',
                version: '1.3.0',
                format: 'image/jpeg'
            }),
            ZM50: L.tileLayer.wms('https://geoportal.cuzk.cz/WMS_ZM50_PUB/WMService.aspx',{
                layers: 'GR_ZM50',
                version: '1.3.0',
                format: 'image/png'
            })
        };

    var overlayers = {
        IIIVM1910: L.tileLayer.wms('https://mapserver.zcu.cz/geoserver/III_vojenske_mapovani/wms?', {
            layers: 'III_vojenske_mapovani:Mapy_roky_vydani_1910-1919',
            version: '1.3.0',
            format: 'image/png8',
            transparent: true,
            tiled: true
        }),
        IIIVM1920: L.tileLayer.wms('https://mapserver.zcu.cz/geoserver/III_vojenske_mapovani/wms?', {
            layers: 'III_vojenske_mapovani:Mapy_roky_vydani_1920-1929',
            version: '1.3.0',
            format: 'image/png8',
            transparent: true,
            tiled: true
        }),
        IIIVM1930: L.tileLayer.wms('https://mapserver.zcu.cz/geoserver/III_vojenske_mapovani/wms?', {
            layers: 'III_vojenske_mapovani:Mapy_roky_vydani_1930-1939',
            version: '1.3.0',
            format: 'image/png8',
            transparent: true,
            tiled: true
        }),
        IIIVM1940: L.tileLayer.wms('https://mapserver.zcu.cz/geoserver/III_vojenske_mapovani/wms?', {
            layers: 'III_vojenske_mapovani:Mapy_roky_vydani_1940-1949',
            version: '1.3.0',
            format: 'image/png8',
            transparent: true,
            tiled: true
        })
    };

    L.control.layers(basemaps, overlayers).addTo(mymap);
    basemaps.ZM50.addTo(mymap);
    
    L.control.scale({imperial: false, position: 'bottomright'}).addTo(mymap);
    
    // center map
    mymap.setView(L.latLng(centr_lat, centr_lon), 2);
    
    mymap.on('click', onMapClick);

    // add definition for Krovak
    proj4.defs("EPSG:5514","+proj=krovak +lat_0=49.5 +lon_0=24.83333333333333 +alpha=30.28813972222222 +k=0.9999 +x_0=0 +y_0=0 "+
                            "+ellps=bessel +towgs84=572.213,85.334,461.94,-4.9732,-1.529,-5.2484,3.5378 +units=m +no_defs");

    // add style for Polygons and Lines
    var myStyle = {
        "color": "#ff7800",
        "weight": 5,
        "opacity": 0.65
    };
    
    // add style for Points
    var geojsonMarkerOptions = {
        radius: 8,
        fillColor: "#ff7800",
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
    };

    layerGeoJsonGeog = L.geoJSON().addTo(mymap);
    layerGeoJsonProj = L.Proj.geoJson(false, {
        onEachFeature: onEachFeature,
        style: myStyle,
        pointToLayer: function (feature, latlng) {
            return L.circleMarker(latlng, geojsonMarkerOptions);
        }
    }).addTo(mymap);
}

/**
 * Function adds layer with GeoJSON data
 * @param geoJson
 */
function addGeoData(geoJson){
    layerGeoJsonProj.clearLayers();
    layerGeoJsonProj.addData(geoJson);
    mymap.fitBounds(layerGeoJsonProj.getBounds());

}

/**
 * Function removes data from GeoJSON layer
 */
function clearGeoJsonLayer(){
    layerGeoJsonProj.clearLayers();
    mymap.setView(L.latLng(centr_lat, centr_lon), 2);
}

/**
 * Function provides onclick event to show popup window with GeoJSON properties
 * @param feature - feature that was clicked
 * @param layer - layer to add popup
 */
function onEachFeature(feature, layer) {
    var popupContent = '<table class="pop_info">';
    if (feature.properties) {
        for(var prop in feature.properties){
            popupContent += '<tr><td class="info_key"><b>' + prop + '</b></td><td>'+ feature.properties[prop] + '</td></tr>';
        }
        popupContent += '</table>';
        layer.bindPopup(popupContent);
    }
}

/**
 * Function handles clicking on map
 * @param e
 */
function onMapClick(e) {
    var pos_wgs = e.latlng;
    var pos_proj = crs_krovak.project(pos_wgs)
    
    var popup = L.popup()
        .setLatLng(e.latlng)
        .setContent("Klikli jste na bod: "+pos_wgs.toString()+", Krovak: "+ pos_proj.toString())
        .openOn(mymap);
};