import {FeatureFlag, FeatureFlags} from "./FeatureFlags";
import nock from "nock";

describe("FeatureFlags", () => {
    const baseUrl = "http://localhsot:3001/api";
    const featureFlags = new FeatureFlags(baseUrl);
    describe("fetchAll", () => {
        it("should fetchAll return data when upstream success", async () => {
            nock(baseUrl)
                .get("/feature-flags/page")
                .query(true)
                .reply(200, {
                    total: 10,
                    page: 1,
                    pageSize: 10,
                    content: [{
                        featureKey: "test",
                        description: {
                            name: "test",
                            description: "test",
                            dataType: "test",
                            defaultValue: "test",
                            status: "PUBLISHED",
                            template: {}
                        }
                    }]
                }, {"content-type": "application/json"})
            const data = await featureFlags.fetchAll(1, 10);
            expect(data.total).toEqual(10);
            expect(data.page).toEqual(1);
            expect(data.pageSize).toEqual(10);
        })

        it("should fetchAll throw error when upstream error", async () => {
            nock(baseUrl)
                .get("/feature-flags/page")
                .query(true)
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.fetchAll(1, 10)).rejects.toThrow(Error);
        })
    })

    describe("fetch", () => {
        it("should fetch return data when upstream success", async () => {
            nock(baseUrl)
                .get("/feature-flags/test")
                .reply(200, {
                    featureKey: "test",
                    description: {
                        name: "test",
                        description: "test",
                        dataType: "test",
                        defaultValue: "test",
                        status: "PUBLISHED",
                        template: {}
                    }
                }, {"content-type": "application/json"})
            const data = await featureFlags.fetch("test");
            expect(data.featureKey).toEqual("test");
        })

        it("should fetch throw error when upstream error", async () => {
            nock(baseUrl)
                .get("/feature-flags/test")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.fetch("test")).rejects.toThrow(Error);
        })
    })

    describe("create", () => {
        it("should create return data when upstream success", async () => {
            nock(baseUrl)
                .post("/feature-flags")
                .reply(200, {
                    featureKey: "test",
                    description: {
                        name: "test",
                        description: "test",
                        dataType: "test",
                        defaultValue: "test",
                        status: "PUBLISHED",
                        template: {}
                    }
                }, {"content-type": "application/json"})
            const data = await featureFlags.create({
                featureKey: "test",
                description: {
                    name: "test",
                    description: "test",
                    dataType: "test",
                    defaultValue: "test",
                    status: "PUBLISHED",
                    template: {}
                }
            } as FeatureFlag);
            expect(data.featureKey).toEqual("test");
        })

        it("should create throw error when upstream error", async () => {
            nock(baseUrl)
                .post("/feature-flags")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.create({
                featureKey: "test",
                description: {
                    name: "test",
                    description: "test",
                    dataType: "test",
                    defaultValue: "test",
                    status: "PUBLISHED",
                    template: {}
                }
            } as FeatureFlag)).rejects.toThrow(Error);
        })
    })

    describe("update", () => {
        it('should update return data when upstream success', async () => {
            nock(baseUrl)
                .put("/feature-flags/test")
                .reply(200, {}, {"content-type": "application/json"})
            await featureFlags.update({
                featureKey: "test",
                description: {
                    name: "test",
                    description: "test",
                    dataType: "test",
                    defaultValue: "test",
                    status: "PUBLISHED",
                    template: {}
                }
            } as FeatureFlag);
        });

        it('should update throw error when upstream error', async () => {
            nock(baseUrl)
                .put("/feature-flags/test")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.update({
                featureKey: "test",
                description: {
                    name: "test",
                    description: "test",
                    dataType: "test",
                    defaultValue: "test",
                    status: "PUBLISHED",
                    template: {}
                }
            } as FeatureFlag)).rejects.toThrow(Error);
        });
    })

    describe("delete", () => {
        it('should delete return data when upstream success', async () => {
            nock(baseUrl)
                .delete("/feature-flags/test")
                .reply(200, {}, {"content-type": "application/json"})
            await featureFlags.delete("test");
        });

        it('should delete throw error when upstream error', async () => {
            nock(baseUrl)
                .delete("/feature-flags/test")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.delete("test")).rejects.toThrow(Error);
        });
    })

    describe("publish", () => {
        it('should publish return data when upstream success', async () => {
            nock(baseUrl)
                .put("/feature-flags/test/publish")
                .reply(200, {}, {"content-type": "application/json"})
            await featureFlags.publish("test");
        });

        it('should publish throw error when upstream error', async () => {
            nock(baseUrl)
                .put("/feature-flags/test/publish")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.publish("test")).rejects.toThrow(Error);
        });
    })

    describe("disable", () => {
        it('should disable return data when upstream success', async () => {
            nock(baseUrl)
                .put("/feature-flags/test/disable")
                .reply(200, {}, {"content-type": "application/json"})
            await featureFlags.disable("test");
        });

        it('should disable throw error when upstream error', async () => {
            nock(baseUrl)
                .put("/feature-flags/test/disable")
                .reply(500, {}, {"content-type": "application/json"})
            await expect(featureFlags.disable("test")).rejects.toThrow(Error);
        });
    })
})