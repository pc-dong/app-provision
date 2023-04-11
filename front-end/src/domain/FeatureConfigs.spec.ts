import { FeatureConfig, FeatureConfigs } from "./FeatureConfigs";
import nock from "nock";

describe("FeatureConfig", () => {
  const baseUrl = "http://localhost:3002/api";
  const featureConfigs = new FeatureConfigs(baseUrl);

  describe("fetch all", () => {
    it("should return a page response", async () => {
      nock(baseUrl)
        .get("/configurations/page")
        .query(true)
        .reply(200, {
          total: 1,
          page: 0,
          pageSize: 10,
          content: [
            {
              id: "test",
              type: "test",
              key: "test",
              data: {},
            },
          ],
        });
      const pageResponse = await featureConfigs.fetchAll("test", 1, 10, "key");
      expect(pageResponse).toBeDefined();
      expect(pageResponse.page).toBe(0);
      expect(pageResponse.pageSize).toBe(10);
      expect(pageResponse.total).toBe(1);
      expect(pageResponse.content).toBeDefined();
      expect(pageResponse.content.length).toBe(1);
    });

    it("should return a page response with empty content", async () => {
      nock(baseUrl).get("/configurations/page").query(true).reply(200, {
        total: 0,
        page: 0,
        pageSize: 10,
        content: [],
      });
      const pageResponse = await featureConfigs.fetchAll("test", 1, 10, "key");
      expect(pageResponse).toBeDefined();
      expect(pageResponse.page).toBe(0);
      expect(pageResponse.pageSize).toBe(10);
      expect(pageResponse.total).toBe(0);
      expect(pageResponse.content).toBeDefined();
      expect(pageResponse.content.length).toBe(0);
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).get("/configurations/page").query(true).reply(500, {
        total: 0,
        page: 0,
        pageSize: 10,
        data: [],
      });
      await expect(
        featureConfigs.fetchAll("test", 1, 10, "key")
      ).rejects.toThrow();
    });
  });

  describe("fetch by id", () => {
    it("should fetch by id return content", async () => {
      nock(baseUrl).get("/configurations/test").reply(200, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      const config = await featureConfigs.fetchById("test");
      expect(config).toBeDefined();
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).get("/configurations/test").reply(500, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      await expect(featureConfigs.fetchById("test")).rejects.toThrow();
    });
  });

  describe("create", () => {
    it("should create return content", async () => {
      nock(baseUrl).post("/configurations").reply(200, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      const config = await featureConfigs.create({
        type: "test",
        key: "test",
        data: {},
      } as FeatureConfig);

      expect(config).toBeDefined();
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).post("/configurations").reply(500, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      await expect(
        featureConfigs.create({
          type: "test",
          key: "test",
          data: {},
        } as FeatureConfig)
      ).rejects.toThrow();
    });
  });

  describe("update", () => {
    it("should update success when api status is 200", async () => {
      nock(baseUrl).put("/configurations/test").reply(200, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      await featureConfigs.update("test", {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      } as FeatureConfig);
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).put("/configurations/test").reply(500, {
        id: "test",
        type: "test",
        key: "test",
        data: {},
      });

      await expect(
        featureConfigs.update("test", {
          id: "test",
          type: "test",
          key: "test",
          data: {},
        } as FeatureConfig)
      ).rejects.toThrow();
    });
  });

  describe("delete", () => {
    it("should delete success when api status is 200", async () => {
      nock(baseUrl).delete("/configurations/test").reply(200);

      await featureConfigs.delete("test");
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).delete("/configurations/test").reply(500);

      await expect(featureConfigs.delete("test")).rejects.toThrow();
    });
  });

  describe("publish", () => {
    it("should publish success when api status is 200", async () => {
      nock(baseUrl).post("/configurations/test/publish").reply(200);

      await featureConfigs.publish("test");
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).post("/configurations/test/publish").reply(500);

      await expect(featureConfigs.publish("test")).rejects.toThrow();
    });
  });

  describe("disable", () => {
    it("should disable success when api status is 200", async () => {
      nock(baseUrl).post("/configurations/test/disable").reply(200);

      await featureConfigs.disable("test");
    });

    it("should throw error when response is not 200", async () => {
      nock(baseUrl).post("/configurations/test/disable").reply(500);

      await expect(featureConfigs.disable("test")).rejects.toThrow();
    });
  });
});
