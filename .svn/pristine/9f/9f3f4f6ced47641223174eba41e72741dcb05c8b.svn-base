var structure = [
    {
    name: "Data",
    id:"data",
    children: [
        {
        name:"Datasources",
        id:"sources",
        content: {
            type: "list",
            action:"/sources?state=READY",
            method: "get",
            autoupdate: 10,
            buttons: [
                {
                  name: "delete",
                  glyphicon: "glyphicon-remove",
                  confirm: {text: "Really?" , action: "/sources/{0}", method: "DELETE"},
                }
            ],
            detail: {
                action: "/data/sources/{0}",
                type: "json",
                buttons: [

                    ]
                }
            }
        },
        {
        name:"Datasources (Currently connecting)",
        id:"processingsources",
        content: {
            type: "list",
            action:"/sources?state=CONNECTING",
            method: "get",
            autoupdate: 10,
            buttons: [
                {
                  name: "delete",
                  glyphicon: "glyphicon-remove",
                  confirm: {text: "Really?" , action: "/sources/{0}", method: "DELETE"}
                }
            ],
            detail: {
                action: "/sources/{0}"
                }
            }
        },
        {
        name:"Available Connectors",
        id:"connections",
        content: {
            type: "list",
            action:"/sources?state=AVAILABLE",
            autoupdate: 10,
            detail: {
                    action: "/sources/{0}",
                    buttons: [
                        {
                          name: "connect",
                          glyphicon: "glyphicon glyphicon-plus",
                          form: { container: "#detail", action: "/sources", method: "PUT"}
                        }
                    ]
                }
            }
        }
    ],
    contentRef: "sources"
    },
    {
    name: "Recommender",
    id:"rec",
    children: [
      {
            name:"Trained Recommender",
            id:"recs",
            content: {
                type: "list",
                action:"/train?state=READY",
                method: "get",
                autoupdate: 10,
                buttons: [
                    {
                      name: "delete",
                      glyphicon: "glyphicon-remove",
                      confirm: {text: "Really?" , action: "/train/{0}", method: "DELETE"},
                    }
                ],
                detail: {
                    action: "/train/{0}",
                    type: "json"
                }
            }
         },
        {
            name:"Recommender in Training",
            id:"trec",
            content: {
                type: "list",
                action:"/train?state=TRAINING",
                method: "get",
                autoupdate: 10,
                buttons: [
                    {
                      name: "delete",
                      glyphicon: "glyphicon-remove",
                      confirm: {text: "Really?" , action: "/train/{0}", method: "DELETE"},
                    }
                ],
                detail: {
                    action: "/train/{0}",
                    type: "json"
                }
            }
         },
        {
        name:"Available Recommenders",
        id:"arec",
        content: {
            type: "list",
            action:"/train?state=AVAILABLE",
            method: "get",
            autoupdate: 10,
            buttons: [
                {
                  name: "delete",
                  glyphicon: "glyphicon-remove",
                  confirm: {text: "Really?" , action: "/train/{0}", method: "DELETE"},
                }
            ],
            detail: {
                action: "/train/{0}",
                type: "json",
                buttons: [
                        {
                          name: "update",
                          glyphicon: "glyphicon glyphicon-plus",
                          form: {action: "/train", method: "PUT"}
                        }
                    ]
                }
            }
        }
    ],
    contentRef: "recs"
    },
    {
    name: "Experiments",
    id:"experiments",
    children: [
        {
        name:"Finished experiments",
        id:"finished"
        },
        {
        name:"Running experiments",
        id:"running"
        },
        {
        name:"Available DataSplitters",
        id:"splitter"
        },
        {
        name:"Available Metrics",
        id:"metrics"
        }
    ],
     contentRef: "finished"
    }
];
