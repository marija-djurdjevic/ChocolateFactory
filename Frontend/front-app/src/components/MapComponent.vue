<template>
    <div ref="mapContainer" class="map">
      <div class="geocoder-container">
        <input
          type="text"
          placeholder="Search for a location"
          v-model="searchQuery"
          @keyup.enter="searchLocation"
          @keydown.enter.prevent
          class="geocoder-input"
        />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, defineEmits } from 'vue';
  import 'ol/ol.css';
  import { Map, View } from 'ol';
  import TileLayer from 'ol/layer/Tile';
  import OSM from 'ol/source/OSM';
  import { fromLonLat, toLonLat } from 'ol/proj';
  
  const mapContainer = ref(null);
  const map = ref(null);
  const selectedLocation = ref(null);
  const searchQuery = ref('');
  
  const emit = defineEmits(['location-selected']);
  
  onMounted(() => {
    map.value = new Map({
      target: mapContainer.value,
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      view: new View({
        center: fromLonLat([0, 0]),
        zoom: 0,
      }),
    });
  
    map.value.on('click', function (event) {
      const coordinates = toLonLat(event.coordinate);
      fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${coordinates[1]}&lon=${coordinates[0]}`)
        .then(response => response.json())
        .then(data => {
          const road = data.address.road || '';
          const houseNumber = data.address.house_number || '';
          const city = data.address.city || data.address.town || '';
          const postcode = data.address.postcode || '';
          const formattedAddress = `${road} ${houseNumber}, ${city}, ${postcode}`;
          emit('location-selected', {
            longitude: coordinates[0],
            latitude: coordinates[1],
            address: formattedAddress,
          });
          selectedLocation.value = {
            longitude: coordinates[0],
            latitude: coordinates[1],
            address: formattedAddress,
          };
          map.value.getView().animate({ center: event.coordinate, zoom: 17 });
        });
    });
  });
  
  function searchLocation() {
    if (!searchQuery.value) return;
  
    fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(searchQuery.value)}&format=json&limit=1`)
      .then(response => response.json())
      .then(data => {
        if (data.length > 0) {
          const result = data[0];
          const coordinates = [result.lon, result.lat];
          const lonLat = toLonLat(coordinates);
          const formattedAddress = result.display_name;
  
          emit('location-selected', {
            longitude: parseFloat(result.lon),
            latitude: parseFloat(result.lat),
            address: formattedAddress,
          });
          selectedLocation.value = {
            longitude: parseFloat(result.lon),
            latitude: parseFloat(result.lat),
            address: formattedAddress,
          };
          map.value.getView().animate({ center: fromLonLat([parseFloat(result.lon), parseFloat(result.lat)]), zoom: 17 });
        } else {
          alert('Location not found');
        }
      });
  }
  </script>
  
  <style scoped>
  .map {
    position: relative;
    width: 100%;
    height: 400px;
  }
  
  .geocoder-container {
    position: absolute;
    top: 10px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 1000;
    background-color: white;
    padding: 10px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  }
  
  .geocoder-input {
    width: 300px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  </style>
  