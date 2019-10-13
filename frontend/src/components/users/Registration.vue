<template>
    <v-content>
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-form ref="form"
                            v-model="valid"
                            lazy-validation
                            @submit.prevent="doRegister">
                        <v-card class="elevation-12">
                            <v-toolbar dark color="primary">
                                <v-toolbar-title>Registration form</v-toolbar-title>
                            </v-toolbar>
                            <v-card-text>
                                <v-alert v-if="error"
                                         :value="true"
                                         type="error"
                                >
                                    Failed to register. Please try again
                                </v-alert>
                                <v-text-field prepend-icon="person" name="name" label="Name" type="text"
                                              :rules="nameRules"
                                              v-model="user.name"></v-text-field>

                                <v-text-field prepend-icon="email" name="email" label="Email" type="text"
                                              :rules="emailRules"
                                              v-model="user.email"></v-text-field>

                                <v-text-field id="password" prepend-icon="lock" name="password" label="Password"
                                              :rules="passwordRules"
                                              type="password" v-model="user.password"></v-text-field>

                            </v-card-text>
                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn type="submit" color="primary">Register</v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-form>
                </v-flex>
            </v-layout>
        </v-container>
    </v-content>

</template>

<script>
    export default {
        name: 'Registration',
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
                nameRules: [
                    v => !!v || 'Name is required'
                ],
                user: {},
                error: ''
            }
        },
        methods: {
            doRegister () {
                if (this.$refs.form.validate()) {
                    this.$store.dispatch('register', this.user).then(response => {
                        this.$log.info('Registration successful')
                        this.$router.push('/login')
                    }, error => {
                        this.$log.error('Registration failed', error)
                        this.error = 'Registration failed'
                    })
                }
            }
        }
    }
</script>
