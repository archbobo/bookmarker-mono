<template>
    <div>
        <v-toolbar dark color="primary" >
            <v-toolbar-title class="white--text">
                <router-link to="/" class="toolbar-title white--text">BookMarker</router-link>
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-toolbar-items class="hidden-sm-and-down">
                <v-btn flat to="/login" v-if="!isUserLoggedIn">Login</v-btn>
                <v-btn flat to="/registration" v-if="!isUserLoggedIn">Register</v-btn>
                <v-btn flat to="/new-bookmark" v-if="isUserLoggedIn">Add Bookmark</v-btn>
                <v-menu offset-y v-if="isUserLoggedIn">
                    <v-btn
                            slot="activator"
                            color="normal"
                            dark
                            flat
                    >
                        {{loginUser.name}}
                    </v-btn>

                    <v-list>
                        <v-list-tile :to="{name: 'UserProfile', params: {id: loginUser.id}}">
                            <v-list-tile-title >My Profile</v-list-tile-title>
                        </v-list-tile>
                        <v-list-tile @click.prevent="doLogout">
                            <v-list-tile-title>Logout</v-list-tile-title>
                        </v-list-tile>

                    </v-list>
                </v-menu>
            </v-toolbar-items>

            <v-menu class="hidden-md-and-up">
                <v-toolbar-side-icon slot="activator"></v-toolbar-side-icon>
                <v-list>
                    <v-list-tile v-if="!isUserLoggedIn" :to="{path: '/login'}">
                        <v-list-tile-title >Login</v-list-tile-title>
                    </v-list-tile>
                    <v-list-tile v-if="!isUserLoggedIn" :to="{path: '/registration'}">
                        <v-list-tile-title >Register</v-list-tile-title>
                    </v-list-tile>
                    <v-list-tile v-if="isUserLoggedIn" :to="{name: 'UserProfile', params: {id: loginUser.id}}">
                        <v-list-tile-title >My Profile</v-list-tile-title>
                    </v-list-tile>

                    <v-list-tile v-if="isUserLoggedIn" @click.prevent="doLogout">
                        <v-list-tile-title>Logout</v-list-tile-title>
                    </v-list-tile>
                </v-list>
            </v-menu>

        </v-toolbar>
    </div>
</template>

<script>
    export default {
        name: 'NavBar',
        data () {
            return {
                loggedIn: false,
                loginUser: {}
            }
        },
        mounted: function () {
            const accessToken = localStorage.getItem('access_token')
            if (accessToken) {
                this.loggedIn = true
            }
            this.reloadCurrentUser()
            const self = this
            window.eventBus.$on('loggedin', function () {
                this.$log.info('received loggedin emit event')
                self.loggedIn = true
                self.reloadCurrentUser()
            })

            window.eventBus.$on('logout', function () {
                this.$log.info('received logout emit event')
                self.loggedIn = false
                self.loginUser = {}
                self.$store.dispatch('logout')
                self.$router.push('/login')
            })
        },

        methods: {
            doLogout () {
                window.eventBus.$emit('logout')
            },
            reloadCurrentUser () {
                this.loginUser = this.$store.getters.currentUser()
            }
        },

        computed: {
            isUserLoggedIn () {
                return this.loggedIn
            }
        }
    }
</script>
