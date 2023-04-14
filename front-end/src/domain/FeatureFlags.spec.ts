import {
  FeatureFlag,
  FeatureFlags,
  getDefaultValue,
  TemplateDataType,
} from "./FeatureFlags";
import nock from "nock";

describe("FeatureFlags", () => {
  const baseUrl = "http://localhsot:3001/api";
  const featureFlags = new FeatureFlags(baseUrl);
  describe("fetchAll", () => {
    it("should fetchAll return data when upstream success", async () => {
      nock(baseUrl)
        .get("/feature-flags/page")
        .query(true)
        .reply(
          200,
          {
            total: 10,
            page: 1,
            pageSize: 10,
            content: [
              {
                featureKey: "test",
                description: {
                  name: "test",
                  description: "test",
                  dataType: "test",
                  defaultValue: "test",
                  status: "PUBLISHED",
                  template: {},
                },
              },
            ],
          },
          { "content-type": "application/json" }
        );
      const data = await featureFlags.fetchAll(1, 10);
      expect(data.total).toEqual(10);
      expect(data.page).toEqual(1);
      expect(data.pageSize).toEqual(10);
    });

    it("should fetchAll throw error when upstream error", async () => {
      nock(baseUrl)
        .get("/feature-flags/page")
        .query(true)
        .reply(500, {}, { "content-type": "application/json" });
      await expect(featureFlags.fetchAll(1, 10)).rejects.toThrow(Error);
    });
  });

  describe("fetch", () => {
    it("should fetch return data when upstream success", async () => {
      nock(baseUrl)
        .get("/feature-flags/test")
        .reply(
          200,
          {
            featureKey: "test",
            description: {
              name: "test",
              description: "test",
              dataType: "test",
              defaultValue: "test",
              status: "PUBLISHED",
              template: {},
            },
          },
          { "content-type": "application/json" }
        );
      const data = await featureFlags.fetch("test");
      expect(data.featureKey).toEqual("test");
    });

    it("should fetch throw error when upstream error", async () => {
      nock(baseUrl)
        .get("/feature-flags/test")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(featureFlags.fetch("test")).rejects.toThrow(Error);
    });
  });

  describe("create", () => {
    it("should create return data when upstream success", async () => {
      nock(baseUrl)
        .post("/feature-flags")
        .reply(
          200,
          {
            featureKey: "test",
            name: "test",
            description: "test",
            dataType: "test",
            defaultValue: "test",
            status: "PUBLISHED",
            template: { dataType: TemplateDataType.STRING },
          },
          { "content-type": "application/json" }
        );
      const data = await featureFlags.create({
        featureKey: "test",
        name: "test",
        description: "test",
        dataType: "test",
        defaultValue: "test",
        status: "PUBLISHED",
        template: { dataType: TemplateDataType.STRING },
      } as FeatureFlag);
      expect(data.featureKey).toEqual("test");
    });

    it("should create throw error when upstream error", async () => {
      nock(baseUrl)
        .post("/feature-flags")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(
        featureFlags.create({
          featureKey: "test",
          name: "test",
          description: "test",
          status: "PUBLISHED",
          template: { dataType: TemplateDataType.STRING },
        } as FeatureFlag)
      ).rejects.toThrow(Error);
    });
  });

  describe("update", () => {
    it("should update return data when upstream success", async () => {
      nock(baseUrl)
        .put("/feature-flags/test")
        .reply(200, {}, { "content-type": "application/json" });
      await featureFlags.update({
        featureKey: "test",
        name: "test",
        description: "test",
        status: "PUBLISHED",
        template: { dataType: TemplateDataType.STRING },
      } as FeatureFlag);
    });

    it("should update throw error when upstream error", async () => {
      nock(baseUrl)
        .put("/feature-flags/test")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(
        featureFlags.update({
          featureKey: "test",
          name: "test",
          description: "test",
          status: "PUBLISHED",
          template: { dataType: TemplateDataType.STRING },
        } as FeatureFlag)
      ).rejects.toThrow(Error);
    });
  });

  describe("delete", () => {
    it("should delete return data when upstream success", async () => {
      nock(baseUrl)
        .delete("/feature-flags/test")
        .reply(200, {}, { "content-type": "application/json" });
      await featureFlags.delete("test");
    });

    it("should delete throw error when upstream error", async () => {
      nock(baseUrl)
        .delete("/feature-flags/test")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(featureFlags.delete("test")).rejects.toThrow(Error);
    });
  });

  describe("publish", () => {
    it("should publish return data when upstream success", async () => {
      nock(baseUrl)
        .put("/feature-flags/test/publish")
        .reply(200, {}, { "content-type": "application/json" });
      await featureFlags.publish("test");
    });

    it("should publish throw error when upstream error", async () => {
      nock(baseUrl)
        .put("/feature-flags/test/publish")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(featureFlags.publish("test")).rejects.toThrow(Error);
    });
  });

  describe("disable", () => {
    it("should disable return data when upstream success", async () => {
      nock(baseUrl)
        .put("/feature-flags/test/disable")
        .reply(200, {}, { "content-type": "application/json" });
      await featureFlags.disable("test");
    });

    it("should disable throw error when upstream error", async () => {
      nock(baseUrl)
        .put("/feature-flags/test/disable")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(featureFlags.disable("test")).rejects.toThrow(Error);
    });
  });

  describe("getDefaultValue", () => {
    describe("getDefaultValue with STRING type", () => {
      it("should getDefaultValue return '' when defaultValue is undefined", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.STRING,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual("");
      });

      it('should getDefaultValue return "" when defaultValue is null', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.STRING,
            defaultValue: null,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual("");
      });

      it('should getDefaultValue return "" when defaultValue is ""', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.STRING,
            defaultValue: "",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual("");
      });

      it("should getDefaultValue return defaultValue when defaultValue is not empty string", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.STRING,
            defaultValue: "test",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual("test");
      });
    });

    describe("getDefaultValue with BOOLEAN type", () => {
      it("should getDefaultValue return false when defaultValue is undefined", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: undefined,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(false);
      });

      it("should getDefaultValue return false when defaultValue is null", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: null,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(false);
      });

      it("should getDefaultValue return false when defaultValue is false", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: false,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(false);
      });

      it("should getDefaultValue return false when defaultValue is not boolean", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: "test",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(false);
      });

      it("should getDefaultValue return true when defaultValue is true", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: true,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(true);
      });

      it("should getDefaultValue return true when defaultValue is 'true'", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.BOOLEAN,
            defaultValue: "true",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(true);
      });
    });

    describe("getDefaultValue with NUMBER type", () => {
      it("should getDefaultValue return 0 when defaultValue is undefined", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: undefined,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it("should getDefaultValue return 0 when defaultValue is null", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: null,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it('should getDefaultValue return 0 when defaultValue is ""', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: "",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it('should getDefaultValue return 0 when defaultValue is "test"', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: "test",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it('should getDefaultValue return 0 when defaultValue is "0"', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: "0",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it("should getDefaultValue return 0 when defaultValue is 0", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: 0,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(0);
      });

      it("should getDefaultValue return 1 when defaultValue is 1", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: 1,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(1);
      });

      it('should getDefaultValue return 1 when defaultValue is "1"', async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.NUMBER,
            defaultValue: "1",
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(1);
      });
    });

    describe("getDefaultValue with OBJECT type", () => {
      it("should getDefaultValue return {} when items is undefined", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.OBJECT,
            defaultValue: undefined,
            items: undefined,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual({});
      });

      it("should getDefaultValue return {} when items is empty", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          status: "PUBLISHED",
          template: {
            dataType: TemplateDataType.OBJECT,
            defaultValue: undefined,
            items: [],
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual({});
      });

      it("should getDefaultValue return items when items is not empty", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          status: "PUBLISHED",
          template: {
            dataType: TemplateDataType.OBJECT,
            defaultValue: undefined,
            items: [
              {
                key: "test",
                name: "test",
                dataType: TemplateDataType.STRING,
                defaultValue: "test",
              },
              {
                key: "test1",
                name: "test1",
                dataType: TemplateDataType.BOOLEAN,
                defaultValue: "test",
              },
            ],
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual({ test: "test", test1: false });
      });
    });

    describe("getDefaultValue with ARRAY type", () => {
      it("should getDefaultValue return [] when items is undefined", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          template: {
            dataType: TemplateDataType.ARRAY,
            defaultValue: undefined,
            items: undefined,
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual([]);
      });

      it("should getDefaultValue return [] when items is empty", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          status: "PUBLISHED",
          template: {
            dataType: TemplateDataType.ARRAY,
            defaultValue: undefined,
            items: [],
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual([]);
      });

      it("should getDefaultValue return items when items is not empty", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          status: "PUBLISHED",
          template: {
            dataType: TemplateDataType.ARRAY,
            defaultValue: undefined,
            items: [
              {
                key: "",
                name: "",
                dataType: TemplateDataType.STRING,
                defaultValue: "test",
              },
              {
                key: "test2",
                name: "test",
                dataType: TemplateDataType.STRING,
                defaultValue: "test",
              },
            ],
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual(["test"]);
      });

      it("should getDefaultValue return items when items type is OBJECT", async () => {
        const featureFlag = {
          featureKey: "test",
          name: "test",
          status: "PUBLISHED",
          template: {
            dataType: TemplateDataType.ARRAY,
            defaultValue: undefined,
            items: [
              {
                key: "",
                name: "",
                dataType: TemplateDataType.OBJECT,
                defaultValue: "test",
                items: [
                  {
                    key: "test2",
                    name: "test",
                    dataType: TemplateDataType.STRING,
                    defaultValue: "test",
                  },
                ],
              },
              {
                key: "test2",
                name: "test",
                dataType: TemplateDataType.STRING,
                defaultValue: "test",
              },
            ],
          },
        } as FeatureFlag;
        const defaultValue = getDefaultValue(featureFlag);
        expect(defaultValue).toEqual([{ test2: "test" }]);
      });
    });
  });
});
