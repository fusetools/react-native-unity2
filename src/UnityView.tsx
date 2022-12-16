import * as React from "react"
import { requireNativeComponent, EventSubscription, NativeModules, View, ViewProps } from "react-native"
import { Platform } from "react-native"
import { UnityModule } from "./UnityModule"

const { RNUnity } = NativeModules

export interface UnityViewProps extends ViewProps {
    onMessage?: (message: any) => void
}

export class UnityView extends React.Component<UnityViewProps> {
    private listener?: EventSubscription

    componentDidMount() {
        if (this.props.onMessage) {
            this.listener = UnityModule.addListener(this.props.onMessage)
        }
    }

    componentWillUnmount() {
        this.listener?.remove()
    }

    render() {
        const { ...props } = this.props
        return Platform.OS === "android" ? (
            <UnityAndroidView {...props} />
        ) : (
            <UnityResponderView {...props} />
        )
    }
}

class UnityAndroidView extends React.Component<ViewProps> {
    render() {
        const { ...props } = this.props
        return (
            <View {...props}>
                <NativeUnityView
                    style={{ position: "absolute", left: 0, right: 0, top: 0, bottom: 0 }}
                />
                {this.props.children}
            </View>
        )
    }
}

// @ts-ignore
const NativeUnityView = requireNativeComponent<UnityAndroidViewProps>("UnityView", UnityAndroidView)

class UnityResponderView extends React.Component {
    componentDidMount() {
        RNUnity.initialize()
    }

    componentWillUnmount() {
        RNUnity.unloadUnity()
    }

    render() {
        const { ...props } = this.props
        return (
            <NativeResponderView {...props} />
        )
    }
}

// @ts-ignore
const NativeResponderView = requireNativeComponent("UnityResponderView", UnityResponderView)
