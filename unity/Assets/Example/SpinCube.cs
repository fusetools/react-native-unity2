using System.Collections;
using System.Collections.Generic;
using System;

using UnityEngine;
using RNUnity;

public class SpinCube : MonoBehaviour
{
    bool _rotate;
    bool _scale;

    public Vector3 RotateAmount;

    // Start is called before the first frame update
    void Start()
    {
        _rotate = true;
        _scale = false;
    }

    // Update is called once per frame
    void Update()
    {
        if (_rotate)
            transform.Rotate(RotateAmount);
    }

    void setColor(string input)
    {
        Color color;
        if (ColorUtility.TryParseHtmlString(input, out color))
            GetComponent<Renderer>().material.color = color;
    }

    void toggleRotate()
    {
        _rotate = !_rotate;
    }

    void toggleScale()
    {
        _scale = !_scale;
        transform.localScale *= _scale ? 2.0f : 0.5f;
    }

    void OnMouseDown()
    {
        RNBridge.SendMessage("click!");
        toggleScale();
    }

    // Call the following methods from your RN app

    void toggleRotateRN(object param)
    {
        toggleRotate();
        RNPromise
            .Begin(param)
            .Resolve(_rotate);
    }

    void getAccountRN(object param)
    {
        RNPromise
            .Begin(param)
            .Resolve(new Account
            {
                Email = "james@example.com",
                Active = true,
                CreatedDate = new DateTime(2013, 1, 20, 0, 0, 0, DateTimeKind.Utc),
                Roles = new List<string>
                {
                    "User",
                    "Admin"
                }
            });
    }

    void failRN(object param)
    {
        RNPromise
            .Begin(param)
            .Reject("This doesn't work");
    }

    void setColorRN(object param)
    {
        var promise = RNPromise<string>.Begin(param);
        setColor(promise.input);
        promise.Resolve();
    }
}

public class Account
{
    public string Email { get; set; }
    public bool Active { get; set; }
    public DateTime CreatedDate { get; set; }
    public IList<string> Roles { get; set; }
}
