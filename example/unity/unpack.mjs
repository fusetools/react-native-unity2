import tar from "tar"
import path from "path"
import {fileURLToPath} from "url"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

console.log("Unpacking Unity files in", __dirname)

tar.x({C: `${__dirname}/android`, file: `${__dirname}/android.tgz`, sync: true})
tar.x({C: `${__dirname}/ios`, file: `${__dirname}/ios.tgz`, sync: true})
