import {
  FeatureFlag,
  FeatureFlagDescription,
  FeatureFlags,
  getDefaultValue,
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
      const data = await featureFlags.create({
        featureKey: "test",
        description: {
          name: "test",
          description: "test",
          dataType: "test",
          defaultValue: "test",
          status: "PUBLISHED",
          template: {},
        },
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
          description: {
            name: "test",
            description: "test",
            dataType: "test",
            defaultValue: "test",
            status: "PUBLISHED",
            template: {},
          },
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
        description: {
          name: "test",
          description: "test",
          dataType: "test",
          defaultValue: "test",
          status: "PUBLISHED",
          template: {},
        },
      } as FeatureFlag);
    });

    it("should update throw error when upstream error", async () => {
      nock(baseUrl)
        .put("/feature-flags/test")
        .reply(500, {}, { "content-type": "application/json" });
      await expect(
        featureFlags.update({
          featureKey: "test",
          description: {
            name: "test",
            description: "test",
            dataType: "test",
            defaultValue: "test",
            status: "PUBLISHED",
            template: {},
          },
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
    describe("getDefaultValue dataType is BOOLEAN", () => {
      it("should return false when template.dataType is BOOLEAN and defaultValue is null", () => {
        const description = {
          dataType: "BOOLEAN",
          defaultValue: null,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(false);
      });

      it("should return false when description.dataType is BOOLEAN and defaultValue is 'false'", () => {
        const description = {
          dataType: "BOOLEAN",
          defaultValue: "false",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(false);
      });

      it("should return false when description.dataType is BOOLEAN and defaultValue is false", () => {
        const description = {
          dataType: "BOOLEAN",
          defaultValue: false,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(false);
      });

      it("should return true when description.dataType is BOOLEAN and defaultValue is 'true'", () => {
        const description = {
          dataType: "BOOLEAN",
          defaultValue: "true",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(true);
      });

      it("should return true when description.dataType is BOOLEAN and defaultValue is 'true'", () => {
        const description = {
          dataType: "BOOLEAN",
          defaultValue: true,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(true);
      });
    });

    describe("getDefaultValue dataType is STRING", () => {
      it("should return empty string when description.dataType is STRING and defaultValue is null", () => {
        const description = {
          dataType: "STRING",
          defaultValue: null,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("");
      });

      it("should return empty string when description.dataType is STRING and defaultValue is empty string", () => {
        const description = {
          dataType: "STRING",
          defaultValue: "",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("");
      });

      it("should return string when description.dataType is STRING and defaultValue is not empty string", () => {
        const description = {
          dataType: "STRING",
          defaultValue: "test",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("test");
      });
    });

    describe("getDefaultValue dataType is NUMBER", () => {
      it("should return 0 when description.dataType is NUMBER and defaultValue is null", () => {
        const description = {
          dataType: "NUMBER",
          defaultValue: null,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(0);
      });

      it("should return 0 when description.dataType is NUMBER and defaultValue is empty string", () => {
        const description = {
          dataType: "NUMBER",
          defaultValue: "",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(0);
      });

      it("should return 0 when description.dataType is NUMBER and defaultValue is not number", () => {
        const description = {
          dataType: "NUMBER",
          defaultValue: "test",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(0);
      });

      it("should return number when description.dataType is NUMBER and defaultValue is number", () => {
        const description = {
          dataType: "NUMBER",
          defaultValue: 1,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual(1);
      });
    });

    describe("getDefaultValue dataType is JSON_STRING", () => {
      it('should return "{}" when description.dataType is JSON_STRING and defaultValue is null', () => {
        const description = {
          dataType: "JSON_STRING",
          defaultValue: null,
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("{}");
      });

      it('should return "{}" when description.dataType is JSON_STRING and defaultValue is empty string', () => {
        const description = {
          dataType: "JSON_STRING",
          defaultValue: "",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("{}");
      });

      it('should return "{}" when description.dataType is JSON_STRING and defaultValue is not json string', () => {
        const description = {
          dataType: "JSON_STRING",
          defaultValue: "test",
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual("{}");
      });

      it("should return json string when description.dataType is JSON_STRING and defaultValue is json string", () => {
        const description = {
          dataType: "JSON_STRING",
          defaultValue: '{"test": "test"}',
        };

        const featureFlag = {
          featureKey: "test",
          description: description,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual('{"test": "test"}');
      });
    });

    describe("getDefaultValue dataType is JSON", () => {
      it("should return {} when description.dataType is JSON and template is null", () => {
        const description = {
          dataType: "JSON",
          defaultValue: null,
          template: null,
        };

        const featureFlag = {
          featureKey: "test",
          description: description as unknown as FeatureFlagDescription,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual({});
      });

      it("should return {} when description.dataType is JSON and template.items is empty", () => {
        const description = {
          dataType: "JSON",
          defaultValue: null,
          template: {
            items: [],
          },
        };

        const featureFlag = {
          featureKey: "test",
          description: description as unknown as FeatureFlagDescription,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual({});
      });

      it("should return object when description.dataType is JSON and template.items is not empty", () => {
        const description = {
          dataType: "JSON",
          defaultValue: null,
          template: {
            items: [
              {
                key: "test",
                name: "test",
                description: "test",
                dataType: "OBJECT",
                defaultValue: "test",
                subItems: [
                  {
                    key: "test",
                    name: "test",
                    description: "test",
                    dataType: "STRING",
                    defaultValue: "test",
                    subItems: [],
                  },
                ],
              },
              {
                key: "test1",
                name: "test",
                description: "test",
                dataType: "LIST_OBJECT",
                defaultValue: "test",
                subItems: [
                  {
                    key: "test",
                    name: "test",
                    description: "test",
                    dataType: "STRING",
                    defaultValue: "test",
                    subItems: [],
                  },
                ],
              },
              {
                key: "test2",
                name: "test",
                description: "test",
                dataType: "NUMBER",
                defaultValue: "0",
                subItems: [],
              },
              {
                key: "test3",
                name: "test",
                description: "test",
                dataType: "STRING",
                defaultValue: "test",
                subItems: [],
              },
              {
                key: "test4",
                name: "test",
                description: "test",
                dataType: "BOOLEAN",
                defaultValue: "false",
                subItems: [],
              },
              {
                key: "test5",
                name: "test",
                description: "test",
                dataType: "LIST_NUMBER",
                defaultValue: "",
                subItems: [],
              },
              {
                key: "test6",
                name: "test",
                description: "test",
                dataType: "LIST_STRING",
                defaultValue: "",
                subItems: [],
              },
            ],
          },
        };

        const featureFlag = {
          featureKey: "test",
          description: description as unknown as FeatureFlagDescription,
        } as FeatureFlag;
        expect(getDefaultValue(featureFlag)).toEqual({
          test: {
            test: "test",
          },
          test1: [
            {
              test: "test",
            },
          ],
          test2: "0",
          test3: "test",
          test4: false,
          test5: [0],
          test6: [""],
        });
      });
    });
  });
});
