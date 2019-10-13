<template>
    <v-content>
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-form ref="form"
                            v-model="valid"
                            lazy-validation
                            @submit.prevent="doLogin">
                        <v-card class="elevation-12">
                            <v-toolbar dark color="primary">
                                <v-toolbar-title>Login form</v-toolbar-title>
                            </v-toolbar>
                            <v-card-text>
                                <v-alert v-if="error" :value="true" type="error">
                                    Invalid credentials. Please try again
                                </v-alert>
                                <v-text-field prepend-icon="email" name="login" label="Email" type="text"
                                              :rules="emailRules"
                                              v-model="credentials.username"></v-text-field>
                                <v-text-field id="password" prepend-icon="lock" name="password" label="Password"
                                              :rules="passwordRules"
                                              type="password" v-model="credentials.password"></v-text-field>

                            </v-card-text>
                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn type="submit" color="primary">Login</v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-form>
                </v-flex>
            </v-layout>
        </v-container>
    </v-content>
</template>

<script>
    import { mapActions } from 'vuex'
    export default {
        name: 'Login',
        data () {
            return {
                valid: true,
                emailRules: [
                    v => !!v || 'E-mail is required',
                    v => /.+@.+/.test(v) || 'E-mail must be valid'
                ],
                passwordRules: [
                    v => !!v || 'Password is required'
                ],
                credentials: {},
                error: ''
            }
        },
        methods: {
            ...mapActions([ 'login', 'fetchCurrentUser' ]),
            doLogin () {
                if (this.$refs.form.validate()) {
                    this.login(this.credentials).then(response => {
                        this.$log.debug('Login successful')
                        this.fetchCurrentUser().then(resp => {
                            window.eventBus.$emit('loggedin')
                            this.$router.push('/bookmarks')
                        })
                    }, error => {
                        this.$log.error('Login failed', error)
                        this.error = 'Login failed'
                    })
                }
            }
        }
    }
</script>
