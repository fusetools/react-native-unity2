import tar from "tar"
import path from "path"
import rimraf from "rimraf"
import {fileURLToPath} from "url"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

console.log("Unpacking Unity files in", __dirname)

// Delete any old files
rimraf.sync(`${__dirname}/android`)
rimraf.sync(`${__dirname}/ios`)

// Unpack tarballs
tar.x({C: __dirname, file: `${__dirname}/android.tgz`, sync: true})
tar.x({C: __dirname, file: `${__dirname}/ios.tgz`, sync: true})
