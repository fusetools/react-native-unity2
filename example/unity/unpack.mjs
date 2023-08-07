import path from "path"
import rimraf from "rimraf"
import {fileURLToPath} from "url"
import {spawn} from "child_process"

// This script unzips iOS frameworks relevant on macOS;
// other platforms can skip this step
if (process.platform !== "darwin")
    process.exit(0)

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

/** Unzips a zip-file using the system command "unzip" */
function unzip(filename) {
    const cwd = path.dirname(filename)
    const zip = path.basename(filename)
    return new Promise(resolve => {
        spawn("unzip", [zip], {cwd, stdio: "inherit"})
            .on("exit", resolve)
    })
}

console.log("Unzipping iOS frameworks")

// Delete existing frameworks
rimraf.sync(`${__dirname}/ios/UnityFramework.xcframework/ios-arm64/UnityFramework.framework`)
rimraf.sync(`${__dirname}/ios/UnityFramework.xcframework/ios-x86_64-simulator/UnityFramework.framework`)

// Unzip frameworks
await unzip(`${__dirname}/ios/UnityFramework.xcframework/ios-arm64/UnityFramework.framework.zip`)
await unzip(`${__dirname}/ios/UnityFramework.xcframework/ios-x86_64-simulator/UnityFramework.framework.zip`)
