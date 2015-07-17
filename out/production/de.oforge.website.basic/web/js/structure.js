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
                  name: "update",
                  dialog: {settings: "/TODO" , action: "/TODO"},
                },
                {
                  name: "delete",
                  confirm: {text: "Some text" , action: "/TODO"},
                }
            ]
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
                  confirm: {text: "Some text" , action: "/TODO"},
                }
            ]
            }
        },
        {
        name:"Available Connectors",
        id:"connections",
        content: {
            type: "list",
            action:"/sources?state=AVAILABLE",
            autoupdate: 10,
            buttons: [
                {
                  name: "update",
                  glyphicon: "glyphicon glyphicon-refresh",
                  dialog: {settings: "/TODO" , action: "/TODO"},
                },
                {
                  glyphicon: "glyphicon-remove",
                  name: "delete",
                  confirm: {text: "Some text" , action: "/TODO"},
                }
            ]
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
        name:"Trained Recommenders",
        id:"recs"
        },
        {
        name:"Available Recommenders",
        id:"arec"
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
