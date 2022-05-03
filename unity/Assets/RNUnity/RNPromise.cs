using System.Runtime.InteropServices;
using System;

using Newtonsoft.Json;
using UnityEngine;

namespace RNUnity
{
    public class RNPromise
    {
        public object handle;

        public static RNPromise Begin(object param)
        {
            return RNPromise<object>.Begin(param);
        }

        public void Reject<TReason>(TReason reason)
        {
            RNBridge.EmitEvent("reject", new {
                handle = this.handle,
                reason = reason
            });
        }

        public void Resolve<TRetval>(TRetval retval)
        {
            RNBridge.EmitEvent("resolve", new {
                handle = this.handle,
                retval = retval
            });
        }

        public void Resolve()
        {
            RNBridge.EmitEvent("resolve", new {
                handle = this.handle
            });
        }
    }

    public class RNPromise<T> : RNPromise
    {
        public T input;

        public static new RNPromise<T> Begin(object param)
        {
            if (Application.isEditor)
                return new RNPromise<T>();

            if (Debug.isDebugBuild)
                Debug.Log($"{nameof(RNPromise)}: begin");

            try
            {
                return JsonConvert.DeserializeObject<RNPromise<T>>(
                    (string) param
                );
            }
            catch (Exception e)
            {
                Debug.LogError($"{nameof(RNPromise)}: {e.Message}");
                return new RNPromise<T>();
            }
        }
    }
}
