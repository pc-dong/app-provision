import { createStore } from 'vuex'
import axios from "axios";

const SERVER_HOST = "http://localhost:8088"
const featureFlagStore = createStore({
    state() {
        return {
            featureFlags: [],
            total: 0,
            page: 0,
            pageSize: 10,
            addForm: {
                featureKey: '',
                description: {
                    name: '',
                    description: '',
                    dataType: '',
                    status: 'PUBLISHED',
                    defaultValue: ''
                }
            }
        }
    },
    mutations: {
        loadData(state, payload) {
            state.featureFlags = payload.content;
            state.total = payload.total;
            state.page = payload.page + 1
            state.pageSize = payload.pageSize;
        },
        resetAddForm(state) {
            state.addForm = {
                featureKey: '',
                description: {
                    name: '',
                    description: '',
                    dataType: '',
                    status: 'PUBLISHED',
                    defaultValue: ''
                }
            }
        }
    },
    actions: {
        fetchFeatureFlags(context) {
             axios.get(SERVER_HOST + '/feature-flags/page?page=' + (context.state.page > 1 ?  context.state.page - 1 : 0) + '&pageSize=' + context.state.pageSize)
                .then(response => {context.commit("loadData", response.data)})
                .catch(error => console.log(error))
        },
        async submitAddForm(context) {
           return axios.post(SERVER_HOST + '/feature-flags', context.state.addForm)
        },
        async submitEditForm(context) {
            return axios.put(SERVER_HOST + '/feature-flags/' + context.state.addForm.featureKey, context.state.addForm.description)
        },
        async publishFeatureFlag(context, featureKey) {
            return axios.put(SERVER_HOST + '/feature-flags/' + featureKey + '/publish')

        },
        async disableFeatureFlag(context, featureKey) {
            return axios.put(SERVER_HOST + '/feature-flags/' + featureKey + '/disable')

        },
        async deleteFeatureFlag(context, featureKey) {
            return axios.delete(SERVER_HOST + '/feature-flags/' + featureKey )
        }
    }
})

export default featureFlagStore