module.exports = {
    preset: '@vue/cli-plugin-unit-jest/presets/typescript-and',
    transform: {
        '^.+\\.vue$': 'vue-jest',
        '.+\\.(css|styl|less|sass|scss|png|jpg|jpeg|svg|ttf|woff|woff2)$': 'jest-transform-stub',
        '^.+\\.tsx?$': 'ts-jest'
    },
    moduleNameMapper: {
        '^@/(.*)$': '<rootDir>/src/$1'
    },
    collectCoverageFrom: [
        'src/**/*.{ts,js,vue}',
        '!src/main.ts',
        '!src/router/index.ts',
        '!src/store/index.ts',
        '!**/node_modules/**'
    ]
}