<template>
    <div id="app">
        <v-app>
            <nav-bar></nav-bar>
            <router-view/>
        </v-app>
    </div>
</template>

<script>
    import NavBar from './components/NavBar.vue'
    import store from './store'

    export default {
        name: 'App',
        components: {
            'nav-bar': NavBar
        },
        store: store
    }
</script>

<style>

</style>
