<template>
  <v-form>
    <v-container>
      <v-layout row wrap>
        <v-flex xs12 sm6>
          <v-text-field v-bind:value="userProfile.name" label="Name" readonly></v-text-field>
        </v-flex>

        <v-flex xs12 sm6>
          <v-text-field v-bind:value="userProfile.email" label="Email" readonly></v-text-field>
        </v-flex>

        <div>
          <h4 class="headline">Bookmarks Created By: {{userProfile.name}}</h4>
          <bookmarks-list v-bind:bookmarks="userBookmarks.bookmarkList"></bookmarks-list>
        </div>
      </v-layout>
    </v-container>
  </v-form>
</template>
<script>
import { mapActions } from "vuex";
import BookmarksList from "../bookmarks/BookmarksList";
export default {
  name: "UserProfile",
  components: {
    "bookmarks-list": BookmarksList
  },
  data() {
    return {
      userProfile: {},
      userBookmarks: {}
    };
  },
  mounted() {
    this.getUserProfile(this.$route.params.id);
  },
  watch: {
    "$route.params.id": function() {
      this.getUserProfile(this.$route.params.id);
    }
  },
  computed: {
    // ...mapState([ 'user' ])
  },
  methods: {
    ...mapActions(["fetchUserProfile", "fetchBookmarksByUser"]),
    getUserProfile(userId) {
      this.fetchUserProfile(userId).then(response => {
        this.userProfile = response;
        this.fetchBookmarksByUser(userId).then(resp => {
          this.userBookmarks = resp;
        });
      });
    }
  }
};
</script>
