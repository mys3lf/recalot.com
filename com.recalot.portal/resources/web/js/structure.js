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
                    type: "json",
                    buttons: [
                        {
                          name: "get",
                          glyphicon: "glyphicon glyphicon-plus",
                          form: {action: "/rec", method: "GET"}
                        }
                    ]
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
            name:"Create Offline Experiment",
            id:"createoffline",
            content: {
                 type: "form",
                 form: [
                    {
                        id: "experiment-id",
                        type: "string"
                    },
                    {
                        id: "source-id",
                        enum: {
                             action: "/sources?state=READY"
                        },
                        type: "enum"
                    },
                    {
                        id: "split-type",
                        enum: ["simple", "n-fold"],
                        type: "enum"
                    },
                    {
                        id: "splitter-id",
                        enum: {
                             action: "/experiments/splitters"
                        },
                        type: "idconfiguration",
                        action: "/experiments/splitters/{0}"
                    },
                    {
                        id: "runThroughAllItems",
                        type: "boolean",
                        value: "false"
                    },
                    {
                        id: "maxRelevantItemCount",
                        type: "number"
                    },
                    {
                        id: "rec-id",
                        content: {
                            type: "idkeyconfiguration",
                            enum: {
                              action: "/train/?state=AVAILABLE",
                            },
                            action: "/train/{0}"
                        },
                        type: "array"
                    },
                    {
                        id: "metric-ids",
                        content: {
                            type: "idkeyconfiguration",
                            enum: {
                              action: "/experiments/metrics/",
                            },
                            action: "/experiments/metrics/{0}"
                        },
                        type: "array"
                    }

                 ],
                 action: {url: "/experiments", method: "PUT"}
            }
        },
        {
        name:"Finished experiments",
        id:"finished",
        content: {
                 type: "list",
                 action:"/experiments/?state=FINISHED",
                 method: "get",
                 autoupdate: 10,
                 buttons: [],
                 detail: {
                     action: "/experiments/{0}",
                     type: "json",
                     buttons: []
                     }
                 }
        },
        {
        name:"Running experiments",
        id:"running",
        content: {
             type: "list",
             action:"/experiments/?state=RUNNING",
             method: "get",
             autoupdate: 10,
             buttons: [],
             detail: {
                 action: "/experiments/{0}",
                 type: "json",
                 buttons: []
                 }
             }
        },
        {
        name:"Available DataSplitters",
        id:"splitter",
        content: {
             type: "list",
             action:"/experiments/splitters",
             method: "get",
             autoupdate: 10,
             buttons: [],
             detail: {
                 action: "/experiments/splitters/{0}",
                 type: "json",
                 buttons: []
                 }
             }
        },
        {
        name:"Available Metrics",
        id:"metrics",
        content: {
            type: "list",
            action:"/experiments/metrics",
            method: "get",
            autoupdate: 10,
            buttons: [],
            detail: {
                action: "/experiments/metrics/{0}",
                type: "json",
                buttons: []
                }
            }
        }
    ],
     contentRef: "createoffline"
    }
];
