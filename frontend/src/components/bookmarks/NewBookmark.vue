<template>
    <v-content>
        <v-container fluid >
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md6>
                    <v-form ref="form" v-model="valid" lazy-validation
                            @submit.prevent="addNewBookmark"
                    >
                        <v-card class="elevation-12">
                            <v-toolbar dark color="primary">
                                <v-toolbar-title>Add New Bookmark</v-toolbar-title>
                            </v-toolbar>
                            <v-card-text>
                                <v-alert v-if="message.text" :value="true" :type="message.type">
                                    {{message.text}}
                                </v-alert>
                                <v-text-field v-model="newBookmark.url"
                                              :rules="urlRules"
                                              placeholder="URL" label="URL" required></v-text-field>
                                <v-text-field v-model="newBookmark.title"
                                              placeholder="Title" label="Title" required
                                ></v-text-field>

                                <v-combobox
                                        v-model="newBookmark.tags"
                                        :items="tagNames"
                                        label="Tags"
                                        chips
                                        clearable
                                        solo
                                        multiple
                                >
                                    <template slot="selection" slot-scope="data">
                                        <v-chip
                                                :selected="data.selected"
                                                close
                                                @input="removeSelectedTag(data.item)"
                                        >
                                            <strong>{{ data.item }}</strong>&nbsp;
                                        </v-chip>
                                    </template>
                                </v-combobox>

                            </v-card-text>
                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn type="submit" color="primary">Save</v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-form>
                </v-flex>
            </v-layout>
        </v-container>
    </v-content>

</template>

<script>
    import { mapActions, mapState } from 'vuex'

    export default {
        name: 'NewBookmark',
        data () {
            return {
                valid: true,
                urlRules: [
                    v => !!v || 'URL is required'
                ],
                newBookmark: {},
                message: {
                    type: 'error',
                    text: ''
                }
            }
        },
        mounted () {
            this.fetchTags()
        },
        computed: {
            ...mapState(['tags']),
            tagNames: function () {
                return this.tags.map(t => t.name)
            }
        },
        methods: {
            ...mapActions(['fetchTags']),
            addNewBookmark () {
                if (this.$refs.form.validate()) {
                    this.$store.dispatch('createBookmark', this.newBookmark).then(response => {
                        this.$log.info('saved bookmark successfully')
                        this.message = {
                            type: 'info',
                            text: 'saved bookmark successfully'
                        }
                        this.$router.push('/')
                    }, error => {
                        this.$log.error('Save failed', error)
                        this.message = {
                            type: 'error',
                            text: 'Failed to save bookmark. Please try again'
                        }
                    })
                }
            },
            removeSelectedTag (item) {
                this.newBookmark.tags.splice(this.newBookmark.tags.indexOf(item), 1)
                this.newBookmark.tags = [...this.newBookmark.tags]
            }
        }
    }
</script>
