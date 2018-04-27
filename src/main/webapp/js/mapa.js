'use strict';

var mymap;

function initializeMap() {
    var crs_krovak = new L.Proj.CRS("EPSG:5514","+proj=krovak +lat_0=49.5 +lon_0=24.83333333333333 +alpha=30.28813972222222 +k=0.9999 +x_0=0 +y_0=0 "+
        "+ellps=bessel +towgs84=572.213,85.334,461.94,-4.9732,-1.529,-5.2484,3.5378 +units=m +no_defs",
        {
            origin: [-920001, -1240000],
            resolutions: [3251.1999, 1625.5999, 812.7999, 406.3999, 203.1999, 101.5999, 50.7999, 25.3999, 12.6999, 6.3499, 3.1749, 1.5874],
            bounds: L.bounds([-920001, -1240000], [-420001, -929999])
        });
    
    mymap = L.map('map_area', {
        crs: crs_krovak
    });
    
    var basemaps = {
            Cenia: L.tileLayer.wms('http://geoportal.gov.cz/ArcGIS/services/CENIA/cenia_rt_III_vojenske_mapovani/MapServer/WMSServer?', {
                layers: '0',
                version: '1.3.0',
                format: 'image/jpeg'
            }),
            ZM50: L.tileLayer.wms('http://geoportal.cuzk.cz/WMS_ZM50_PUB/WMService.aspx',{
                layers: 'GR_ZM50',
                version: '1.3.0',
                format: 'image/png'
            })
        };
    L.control.layers(basemaps).addTo(mymap);
    basemaps.ZM50.addTo(mymap);
    
    mymap.setView(L.latLng(50.0, 15.0), 4); // zoom 8 pro 3857
    
    mymap.on('click', onMapClick);

    proj4.defs("EPSG:5514","+proj=krovak +lat_0=49.5 +lon_0=24.83333333333333 +alpha=30.28813972222222 +k=0.9999 +x_0=0 +y_0=0 "+
                            "+ellps=bessel +towgs84=572.213,85.334,461.94,-4.9732,-1.529,-5.2484,3.5378 +units=m +no_defs");
    var geojson_krovak = {"type" : "FeatureCollection",
                  "crs" : {"type" : "name","properties" : {"name" : "EPSG:5514"}},
                  "features" : [{"type" : "Feature",
                  "properties" : {"kod" : "3401","nazev" : "Domažlice","nutslau" : "CZ0321"},
                  "geometry" : {"type" : "Point", "coordinates" : [ -860381.3, -1093091.34 ]}},
                  {"type" : "Feature",
                   "properties" : {"kod" : "3404","nazev" : "Klatovy","nutslau" : "CZ0322"},
                   "geometry" : {"type" : "Point","coordinates" : [ -827939.2, -1122276.04 ]}
                  }]};
    var geojson_wgs = {"type" : "FeatureCollection",
            "features" : [{"type" : "Feature",
            "properties" : {"kod" : "3401","nazev" : "Domažlice","nutslau" : "CZ0321"},
            "geometry" : {"type" : "Point", "coordinates" : [49.4875175, 12.9082195]}},
            {"type" : "Feature",
             "properties" : {"kod" : "3404","nazev" : "Klatovy","nutslau" : "CZ0322"},
             "geometry" : {"type" : "Point","coordinates" : [49.2727995, 13.4112003]}
            }]};
    
    var geojsonMarkerOptions = {
            radius: 8,
            fillColor: "#ff7800",
            color: "#000",
            weight: 1,
            opacity: 1,
            fillOpacity: 0.8
    };
    
    var layerProjected = L.Proj.geoJson(geojson_krovak, {
        onEachFeature: onEachFeature,
        pointToLayer: function (feature, latlng) {
            return L.circleMarker(latlng, geojsonMarkerOptions);
        }
    }).addTo(mymap);
    
    var layerGeographic = L.geoJson(geojson_wgs).addTo(mymap);
}

function onEachFeature(feature, layer) {
    var lay = layer;
    if (feature.properties) {
        layer.bindPopup("Properties:"+feature.properties.valueOf()); // bindPopup musí obsahovat String
    }
}

function onMapClick(e) {
    var popup = L.popup()
        .setLatLng(e.latlng)
        .setContent("You clicked the map at " + e.latlng.toString())
        .openOn(mymap);
}

$(document).ready(function(){
    initializeMap();
    L.marker([50.0, 15.0]).addTo(mymap).bindPopup("<b>Hello world!</b><br />I am a popup.");
});