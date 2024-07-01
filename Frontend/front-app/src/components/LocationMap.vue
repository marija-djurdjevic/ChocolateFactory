<template>
    <div ref="mapContainer" class="map"></div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue';
  import 'ol/ol.css';
  import { Map, View } from 'ol';
  import TileLayer from 'ol/layer/Tile';
  import OSM from 'ol/source/OSM';
  import { fromLonLat } from 'ol/proj';
  
  const props = defineProps({
    longitude: {
      type: Number,
      required: true,
    },
    latitude: {
      type: Number,
      required: true,
    },
  });
  
  const mapContainer = ref(null);
  const map = ref(null);
  
  onMounted(() => {
    map.value = new Map({
      target: mapContainer.value,
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      view: new View({
        center: fromLonLat([props.longitude, props.latitude]),
        zoom: 17,
      }),
    });
  });
  
  watch(
    () => [props.longitude, props.latitude],
    ([newLongitude, newLatitude]) => {
      if (map.value) {
        const view = map.value.getView();
        view.setCenter(fromLonLat([newLongitude, newLatitude]));
        view.setZoom(14);
      }
    }
  );
  </script>
  
  <style scoped>
.map {
  width: 100%;
  height: 400px; 
  margin-top: 10px;
}
</style>
  