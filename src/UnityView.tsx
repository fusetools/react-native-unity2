import * as React from "react"
import { requireNativeComponent, EventSubscription, NativeModules, View, ViewProps } from "react-native"
import { Platform } from "react-native"
import { UnityModule } from "./UnityModule"

const { RNUnity } = NativeModules

export interface UnityViewProps extends ViewProps {
    /** Called when a message is received from Unity. */
    onMessage?: (message: any) => void

    /** Called when Unity is ready to receive messages. */
    onReady?: () => void
}

export class UnityView extends React.Component<UnityViewProps> {
    private listener?: EventSubscription

    componentDidMount() {
        if (this.props.onMessage) {
            this.listener = UnityModule.addListener(this.props.onMessage)
        }

        if (this.props.onReady) {
            UnityModule.ensureIsReady(this.props.onReady)
        }
    }

    componentWillUnmount() {
        this.listener?.remove()
    }

    render() {
        return Platform.OS === "android" ? (
            <UnityAndroidView {...this.props} />
        ) : (
            <UnityResponderView {...this.props} />
        )
    }
}

class UnityAndroidView extends React.Component<ViewProps> {
    render() {
        return (
            <View {...this.props}>
                <NativeUnityView
                    style={{ position: "absolute", left: 0, right: 0, top: 0, bottom: 0 }}
                />
                {this.props.children}
            </View>
        )
    }
}

const NativeUnityView = requireNativeComponent<ViewProps>("UnityView")

class UnityResponderView extends React.Component {
    componentDidMount() {
        RNUnity.initialize()
        RNUnity.resume()
    }

    componentWillUnmount() {
        RNUnity.pause()
    }

    render() {
        return (
            <NativeResponderView {...this.props} />
        )
    }
}

const NativeResponderView = requireNativeComponent<ViewProps>("UnityResponderView")
