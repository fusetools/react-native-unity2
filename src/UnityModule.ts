import type { EventSubscription } from "react-native"
import { NativeModules, NativeEventEmitter } from "react-native"

const { RNUnity } = NativeModules

export interface UnityModule {
    /**
     * Waits for Unity to be launched and invokes the callback. This means Unity
     * is ready to receive messages.
     */
    ensureIsReady(callback: () => void): void

    /**
     * Listen for messages from Unity.
     */
    addListener(onMessage: (data: any) => void): EventSubscription

    /**
     * Invoke a Unity method returning a promise.
     * @param gameObject The name or path of GameObject.
     * @param methodName The method to invoke.
     * @param input An object to serialize as JSON.
     */
    callMethod(gameObject: string, methodName: string, input?: any): Promise<any>

    /**
     * Send a message to Unity.
     * @param gameObject The name or path of GameObject.
     * @param methodName The method to invoke.
     * @param message The argument to pass.
     */
    sendMessage(gameObject: string, methodName: string, message: string): void

    /**
     * Sets whether the screen should stay on.
     */
    setKeepAwake(enabled: boolean): void
}

class UnityModuleImpl implements UnityModule {
    eventEmitter: NativeEventEmitter

    methodHandle = -1

    isReady = false

    constructor() {
        this.eventEmitter = new NativeEventEmitter(RNUnity)
        const subscription = this.eventEmitter.addListener("ready", () => {
            this.isReady = true
            subscription.remove()
        })
    }

    ensureIsReady(callback: () => void) {
        if (this.isReady) {
            callback()
            return
        }
        const subscription = this.eventEmitter.addListener("ready", () => {
            this.isReady = true
            subscription.remove()
            callback()
        })
    }

    addListener(onMessage: (data: any) => void) {
        return this.eventEmitter.addListener("message", (json: string) => {
            onMessage(JSON.parse(json))
        })
    }

    async callMethod(gameObject: string, methodName: string, input: any) {
        return new Promise((resolve, reject) => {
            const handle = ++this.methodHandle
            const onReject = this.eventEmitter.addListener("reject", json => {
                const args = JSON.parse(json)
                if (args.handle === handle) {
                    onReject.remove()
                    onResolve.remove()
                    reject(args.reason)
                }
            })
            const onResolve = this.eventEmitter.addListener("resolve", json => {
                const args = JSON.parse(json)
                if (args.handle === handle) {
                    onReject.remove()
                    onResolve.remove()
                    resolve(args.retval)
                }
            })
            RNUnity.sendMessage(gameObject, methodName, JSON.stringify({
                handle,
                input
            }))
        })
    }

    sendMessage(gameObject: string, methodName: string, message: string) {
        RNUnity.sendMessage(gameObject, methodName, message)
    }

    setKeepAwake(enabled: boolean) {
        RNUnity.setKeepAwake(enabled)
    }
}

export const UnityModule: UnityModule = new UnityModuleImpl()
